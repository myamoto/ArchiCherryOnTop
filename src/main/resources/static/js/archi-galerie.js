
/* global mxClient, mxUtils, mxEvent, mxEffects */
jQuery(document).ready(function(){
    new MXGalerie().load();
});

var MXGalerie = function(){
    var that = this;

    const c = new ToolUpCommon();
    c.enableBackToTop();
    c.enableSidebarCollapse();
    c.enableIffytip();

    this.graph = null;
    const SELECT_GROUP = "group";
    const SELECT_SCHEMA = "schema";
    const BTN_EXPORT_GRAPH = "exportGraph";
    
    this.load = async function (){
        console.log("MXGalerie load");
        
        if (!mxClient.isBrowserSupported()){
           mxUtils.error('Browser is not supported!', 200, false);
           return;
        }
        
        var model = new mxGraphModel();
        var graph = new mxGraph(document.getElementById('graphContainer'), model);
        that.graph = graph;

        graph.setEnabled(false);

        graph.setHtmlLabels(true);

        graph.isWrapping = function(){
            return true;
        };

        var parent = graph.getDefaultParent();
        var cx = graph.container.clientWidth / 2;
        var cy = graph.container.clientHeight / 2;

        //var cell = graph.insertVertex(parent, '0-0', '0-0', cx - 20, cy - 15, 60, 40);
        // Animates the changes in the graph model
        graph.getModel().addListener(mxEvent.CHANGE, function(sender, evt){
                var changes = evt.getProperty('edit').changes;
                mxEffects.animateChanges(graph, changes);
        });

        document.getElementById(SELECT_GROUP).addEventListener("change", async function(){
             await that.loadGroup();
        });

        document.getElementById(SELECT_SCHEMA).addEventListener("change", async function(){
             that.updateFilterInURL();
             await that.loadSchema();
        });

        document.getElementById(BTN_EXPORT_GRAPH).addEventListener("click", async function(){
            await that.exportGraph();
        });

        await that.loadGraphs();
    };
    
     this.createUrlGraphImg = function() {
        let group = that.getActiveGroup();
        let schema = that.getActiveSchema();
         
        let view = schema.substring(schema.indexOf("/") + 1, schema.length).trim();
        let model = schema.substring(0, schema.indexOf("/") - 1).trim();
        return "/api/archi/v1/mxgraph/img?path=" + encodeURIComponent(group) 
            + "&model=" + encodeURIComponent(model)
            + "&view=" + encodeURIComponent(view);
    };
    
    this.exportGraph = async function (){      
        let result = await c.secureXhrWrapper(that.createUrlGraphImg(), "GET", null, 'blob');
        if(!result.response) return;
        that.saveBlob(result.response, "graph.png");
        
    };
    
    this.saveBlob = function(blob, fileName) {
        var a = document.createElement('a');
        a.href = window.URL.createObjectURL(blob);
        a.download = fileName;
        a.dispatchEvent(new MouseEvent('click'));
    };
    
    this.updateFilterInURL = function (){ 
        window.history.pushState("object or string", "Archi galerie", that.getFilteredUrl());    
    };

    this.getFilteredUrl = function(){
        return "/?"
            + (!c.isNull(that.getActiveGroup()) ? SELECT_GROUP + "=" + encodeURIComponent(that.getActiveGroup()) + "&": "")
            + (!c.isNull(that.getActiveSchema()) ? SELECT_SCHEMA + "=" + encodeURIComponent(that.getActiveSchema()) : "");
    };
    
    this.getActiveSchema = function(){
        return c.getActiveElemVal(SELECT_SCHEMA);
    };
    
    this.getActiveGroup = function(){
        return c.getActiveElemVal(SELECT_GROUP);
    };
    
    this.createUrlGraph = function() {
        let group = that.getActiveGroup();
        let schema = that.getActiveSchema();
         
        let view = schema.substring(schema.indexOf("/") + 1, schema.length).trim();
        let model = schema.substring(0, schema.indexOf("/") - 1).trim();
        return "/api/archi/v1/mxgraph?path=" + encodeURIComponent(group) 
            + "&model=" + encodeURIComponent(model)
            + "&view=" + encodeURIComponent(view);
    };
    
    this.loadSchema = async function(){
        let result = await c.secureXhrWrapper(that.createUrlGraph(), "GET", null, 'blob');
        
       if(!result.response) return;
       let graph = that.graph;
       
       const reader = new FileReader();
       reader.addEventListener('loadend', (e) => {
          const text = e.srcElement.result;
       
          var xmlDoc = mxUtils.parseXml(text);
          var node = xmlDoc.documentElement;
          var dec = new mxCodec(node.ownerDocument);
          graph.getModel().beginUpdate();
          dec.decode(node, graph.getModel());
          graph.refresh();
          graph.getModel().endUpdate();
        });
        reader.readAsText(result.response);
     };
     
     this.loadGroup = async function(){
       let group = that.getActiveGroup();
       
       let result = await c.secureXhrWrapper("/api/archi/v1/group/" + c.escapeHtml(group), "GET");
        
       if(!result.response) return;
       
       let resp = JSON.parse(result.response);
       let schemaEle = document.getElementById(SELECT_SCHEMA);
       schemaEle.innerHTML = "";

       let currentSch = c.getQueryParam(SELECT_SCHEMA, null);
       for(let graph of resp.group.graphs){
         let graphID = graph.graphName;
            for(let view of graph.views){
                let schema = graphID + " / " + view;
                let selected = currentSch ? schema === currentSch : false;
                schemaEle.appendChild(c.createChoice(schema, schema, "graph_" + schema, selected));
            }
        }
        schemaEle.dispatchEvent(new CustomEvent("change"));
     };
    
    this.loadGraphs = async function(){
       
       let result = await c.secureXhrWrapper("/api/archi/v1/group", "GET");
       if(!result.response) return;
       let resp = JSON.parse(result.response);
       let groupEle = document.getElementById(SELECT_GROUP);
       groupEle.innerHTML = "";

       let idx = 0;
       let currentGrp = c.getQueryParam(SELECT_GROUP, null);
       for(let grp of resp.galerie.groups){
          let selected = currentGrp ? grp === currentGrp : (idx++ === resp.galerie.groups.length);
          groupEle.appendChild(c.createChoice(grp, grp, "grp_" + grp, selected));
       }
       groupEle.dispatchEvent(new CustomEvent("change"));
    };
    
};

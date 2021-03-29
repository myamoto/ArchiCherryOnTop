package org.toolup.archi.web.rest.gallery;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.toolup.archi.service.ArchiGraphException;
import org.toolup.archi.web.rest.BaseController;
import org.toolup.archi.web.rest.BaseOut;
import org.toolup.archi.web.rest.gallery.dto.GalerieOut;
import org.toolup.archi.web.rest.gallery.dto.GroupOut;



@RestController
@RequestMapping(BaseController.BASE_ARCHI_TOOLS)
public class ArchiGalerieController {

	private static Logger logger = LoggerFactory.getLogger(ArchiGalerieController.class);
	
	@Autowired
	ArchiGraphProxyService s;
	
	private static final String MXGRAPH =  "/mxgraph";
	private static final String MXGRAPH_IMG =  MXGRAPH + "/img";
	private static final String GROUPS =  "/group";
	private static final String GROUP =  GROUPS + "/{path}";
	
	
	@GetMapping("")
	public ResponseEntity<BaseOut> get(){
		BaseOut basePath = new BaseOut();
		basePath.add(linkTo(methodOn(ArchiGalerieController.class).get()).withSelfRel());
		
		basePath.add(linkTo(methodOn(ArchiGalerieController.class).readGalerie(null)).withRel(GROUPS));
		basePath.add(linkTo(methodOn(ArchiGalerieController.class).readGroup(null, null)).withRel(GROUP));
		basePath.add(linkTo(methodOn(ArchiGalerieController.class).readMxGraph(null, null, null, null)).withRel(MXGRAPH));
		
		
		return new ResponseEntity<>(basePath, HttpStatus.OK);
	}
	
	@GetMapping(value = {GROUP})
	public ResponseEntity<GroupOut> readGroup(HttpServletRequest request,
			@PathVariable(value="path") String path){
		GroupOut result = new GroupOut();
		
		result.add(linkTo(methodOn(ArchiGalerieController.class)
				.readGroup(null, null)).withSelfRel());
		try {
			return new ResponseEntity<>(result.group(s.readGroup(path)), HttpStatus.OK);
		}catch(ArchiGraphException e) {
			printException(e);
			return new ResponseEntity<>(result.message(e.getMessage()), HttpStatus.valueOf(e.getHttpStatus()));
		}catch(RuntimeException e) {
			printException(e);
			return new ResponseEntity<>(result.message(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = {GROUPS })
	public ResponseEntity<GalerieOut> readGalerie(HttpServletRequest request){
		GalerieOut result = new GalerieOut();
		
		result.add(linkTo(methodOn(ArchiGalerieController.class)
				.readGalerie(null)).withSelfRel());
		try {
			return new ResponseEntity<>(result.galerie(s.readGalerie()), HttpStatus.OK);
		}catch(ArchiGraphException e) {
			printException(e);
			return new ResponseEntity<>(result.message(e.getMessage()), HttpStatus.valueOf(e.getHttpStatus()));
		}catch(RuntimeException e) {
			printException(e);
			return new ResponseEntity<>(result.message(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	@GetMapping(value = {MXGRAPH_IMG}, produces="image/png")
	public ResponseEntity<byte[]> readMxGraphImg(HttpServletRequest request,
			@RequestParam(value="path", required = true) String path,
			@RequestParam(value="model", required = true) String model,
			@RequestParam(value="view", required = true) String view){
		try {
			return new ResponseEntity<>(s.readMxGraphImg(path, model, view), HttpStatus.OK);
		}catch(ArchiGraphException e) {
			printException(e);
			return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.valueOf(e.getHttpStatus()));
		}catch(RuntimeException e) {
			printException(e);
			return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = {MXGRAPH}, produces=MediaType.APPLICATION_XML)
	public ResponseEntity<byte[]> readMxGraph(HttpServletRequest request,
			@RequestParam(value="path", required = true) String path,
			@RequestParam(value="model", required = true) String model,
			@RequestParam(value="view", required = true) String view){
		try {
			return new ResponseEntity<>(s.readMxGraph(path, model, view), HttpStatus.OK);
		}catch(ArchiGraphException e) {
			printException(e);
			return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.valueOf(e.getHttpStatus()));
		}catch(RuntimeException e) {
			printException(e);
			return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void printException(Exception e) {
		try(StringWriter sw = new StringWriter();PrintWriter pw = new PrintWriter(sw);){
			e.printStackTrace(pw);
			logger.error("{}", sw);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}		
	}
	
}

package org.toolup.archi.web.rest.gallery;

import org.springframework.stereotype.Service;
import org.toolup.archi.business.galerie.Gallery;
import org.toolup.archi.business.galerie.Group;
import org.toolup.archi.service.ArchiGraphException;
import org.toolup.archi.service.ArchiGraphService;


@Service
public class ArchiGraphProxyService {

	private final ArchiGraphService s = new ArchiGraphService();
	
	public Group readGroup(String path) throws ArchiGraphException {
		return s.readGroup(path);
	}

	public Gallery readGalerie() throws ArchiGraphException {
		return s.readGalery();
	}

	public byte[] readMxGraph(String path, String model, String view) throws ArchiGraphException {
		return s.readMxGraph(path, model, view);
	}

	public byte[] readMxGraphImg(String path, String model, String view) throws ArchiGraphException {
		return s.readMxGraphImg(path, model, view);
	}

	public void config(String gitBaseUrl, String gitPersonalToken, String projectUrl, String workingDir) {
		s.config(gitBaseUrl, gitPersonalToken, projectUrl, workingDir);		
	}
	
}

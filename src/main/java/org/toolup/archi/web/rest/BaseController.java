package org.toolup.archi.web.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.toolup.archi.web.rest.gallery.ArchiGalerieController;

@RestController
@RequestMapping("api")
public class BaseController {

	public static final String BASE_ARCHI_TOOLS = "api/archi/v1";
	
	@GetMapping("")
	public ResponseEntity<BaseOut> get(){
		BaseOut basePath = new BaseOut();
		basePath.add(linkTo(methodOn(BaseController.class).get()).withSelfRel());
		basePath.add(linkTo(methodOn(ArchiGalerieController.class).get()).withRel(BASE_ARCHI_TOOLS));
		return new ResponseEntity<>(basePath, HttpStatus.OK);
	}
}

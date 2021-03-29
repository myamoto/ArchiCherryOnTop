package org.toolup.archi.web.rest.gallery.dto;

import org.springframework.hateoas.RepresentationModel;
import org.toolup.archi.business.galerie.Gallery;

public class GalerieOut extends RepresentationModel<GalerieOut>{

	private String message;
	private Gallery galerie;

	public String getMessage() {
		return message;
	}

	public GalerieOut message(String message) {
		this.message = message;
		return this;
	}

	public Gallery getGalerie() {
		return galerie;
	}

	public GalerieOut galerie(Gallery galerie) {
		this.galerie = galerie;
		return this;
	}

	@Override
	public String toString() {
		return "GalerieOut [message=" + message + ", galerie=" + galerie + "]";
	}
	
	
}

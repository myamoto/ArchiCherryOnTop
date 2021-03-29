package org.toolup.archi.web.rest.gallery.dto;

import org.springframework.hateoas.RepresentationModel;
import org.toolup.archi.business.galerie.Group;

public class GroupOut extends RepresentationModel<GroupOut>{

	private String message;
	private Group group;

	public String getMessage() {
		return message;
	}

	public GroupOut message(String message) {
		this.message = message;
		return this;
	}


	public Group getGroup() {
		return group;
	}

	public GroupOut group(Group group) {
		this.group = group;
		return this;
	}

	@Override
	public String toString() {
		return "GroupOut [message=" + message + ", galerie=" + group + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupOut other = (GroupOut) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}

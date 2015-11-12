package org.sphpp.workflow.data;

public interface InterMapeable<FROM extends InterMapeable<FROM, TO>, TO extends InterMapeable<TO,FROM>>
	extends Identifiable, Linkable<FROM, TO> {
}

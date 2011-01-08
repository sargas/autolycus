/**
 * 
 */
package net.neoturbine.autolycus.internal;

import java.util.List;
import java.util.Collections;
import java.util.Map;

/**
 * @author Joseph Booker
 *
 */
public class ServiceBulletin {
	private final String name;
	private final String subject;
	private final String detail;
	private final String brief;
	private final String priority;
	private final List<Map<String,String>> serviceAffected;
	
	ServiceBulletin(String name, String subject, String detail, String brief, String priority, List<Map<String,String>> serviceAffected) {
		this.name = name;
		this.subject = subject;
		this.detail = detail;
		this.brief = brief;
		this.priority = priority;
		this.serviceAffected = Collections.unmodifiableList(serviceAffected);
	}

	public String getName() {
		return name;
	}

	public String getSubject() {
		return subject;
	}

	public String getDetail() {
		return detail;
	}

	public String getBrief() {
		return brief;
	}

	public String getPriority() {
		return priority;
	}

	public List<Map<String, String>> getServiceAffected() {
		return serviceAffected;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceBulletin [name=").append(name)
				.append(", subject=").append(subject).append(", detail=")
				.append(detail).append(", brief=").append(brief)
				.append(", priority=").append(priority).append("]");
		return builder.toString();
	}
}
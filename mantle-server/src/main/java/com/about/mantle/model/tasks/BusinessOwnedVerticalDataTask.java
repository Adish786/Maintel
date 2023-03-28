package com.about.mantle.model.tasks;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.impl.BusinessOwnedVerticalDataServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class provides access to configuration resources managed by the business outside the release schedule of the
 * associated vertical.
 */
@Tasks
public class BusinessOwnedVerticalDataTask {

	private final BusinessOwnedVerticalDataServiceImpl bovdService;

	private Logger log = LoggerFactory.getLogger(BusinessOwnedVerticalDataTask.class);

	public BusinessOwnedVerticalDataTask(BusinessOwnedVerticalDataServiceImpl bovdService) {
		this.bovdService = bovdService;
	}

	/**
	 * This task returns a BOVD resource.
	 *
	 * @param path The full path of the requested resource
	 */
	@Task(name = "bovdResource")
	public byte[] bovdResource(@TaskParameter(name = "path") String path) {
		byte[] data = null;
		try {
			data = this.bovdService.getResource(path);
		} catch (Exception e) {
			throw new GlobeException("Resource cannot be served for path: " + path, e);
		}

		return data;
	}


	/**
	 * This task responds to an action with a BOVD resource.
	 *
	 * @param path The full path of the requested resource
	 */
	@Task(name = "serveBovdResource")
	public void serveBovdResource(@TaskParameter(name = "path") String path, @TaskParameter(name = "mime") String mime, HttpServletResponse response)
			throws IOException, GlobeException {
		response.setContentType(mime);
		response.getOutputStream().write(bovdResource(path));
	}

	/**
	 * This task responds to an action with a BOVD resource.
	 *
	 * @param path The full path of the requested resource
	 */
	@Task(name = "serveBovdResource")
	public void serveBovdResource(@TaskParameter(name = "path") String path, HttpServletResponse response)
			throws IOException, GlobeException {
		serveBovdResource(path, "application/octet-stream", response);
	}
}
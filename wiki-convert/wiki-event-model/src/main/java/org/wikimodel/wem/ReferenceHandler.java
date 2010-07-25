/**
 * 
 */
package org.wikimodel.wem;

/**
 * This utility class splits references to individual parts (hyper-link and
 * label) and delegates to separate methods handling of images, normal
 * references and downloads.
 * 
 * @author kotelnikov
 */
public abstract class ReferenceHandler {

	public static final String PREFIX_DOWNLOAD = "download:";

	public static final String PREFIX_IMAGE = "image:";

	public void handle(String ref) {
		ref = ref.trim();
		int idx = ref.indexOf(' ');
		String label = "";
		if (idx > 0) {
			label = ref.substring(idx).trim();
			ref = ref.substring(0, idx);
		}

		if (ref.startsWith(PREFIX_IMAGE)) {
			ref = ref.substring(PREFIX_IMAGE.length());
            if (label == null || "".equals(label)) {
                label = ref;
            }
			handleImage(ref, label);
		} else if (ref.startsWith(PREFIX_DOWNLOAD)) {
			ref = ref.substring(PREFIX_DOWNLOAD.length());
            if (label == null || "".equals(label)) {
                label = ref;
            }
			handleDownload(ref, label);
		} else {
			if (label == null || "".equals(label)) {
				label = ref;
			}
			handleReference(ref, label);
		}
	}

	protected void handleDownload(String ref, String label) {
		handleReference(ref, label);
	}

	protected abstract void handleImage(String ref, String label);

	protected abstract void handleReference(String ref, String label);

}

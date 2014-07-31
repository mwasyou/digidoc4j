package org.digidoc4j.api;

import org.apache.commons.io.FilenameUtils;
import org.digidoc4j.ASiCSContainer;
import org.digidoc4j.BDocContainer;
import org.digidoc4j.DDocContainer;
import org.digidoc4j.api.exceptions.DigiDoc4JException;
import org.digidoc4j.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Offers functionality for handling data files and signatures in a container.
 * <p>
 * A container can contain several files and all those files can be signed using signing certificates.
 * A container can only be signed if it contains data files.
 * </p><p>
 * Data files can be added and removed from a container only if the container is not signed.
 * To modify the data list of a signed container by adding or removing datafiles you must first
 * remove all the signatures.
 * </p>
 */
public abstract class Container {
  static final Logger logger = LoggerFactory.getLogger(Container.class);

  /**
   * Create an ASIC_E container.
   *
   * @return new ASIC_E Container
   */
  public static Container create() {
    logger.debug("");
    return create(DocumentType.ASIC_E);
  }

  /**
   * Create a container of the specified type.
   *
   * @param documentType Type of container to create
   * @return new container of the specified format
   */
  public static Container create(DocumentType documentType) {
    logger.debug("");
    Container container = new DDocContainer();
    if (documentType == DocumentType.ASIC_E) {
      container = new BDocContainer();
    } else if (documentType == DocumentType.ASIC_S) {
      container = new ASiCSContainer();
    }

    logger.info("Container with type " + container.getDocumentType() + " has been created");
    return container;
  }

  /**
   * Open container from a file
   *
   * @param path file name and path.
   * @return container
   * @throws org.digidoc4j.api.exceptions.DigiDoc4JException TODO write description
   */
  public static Container open(String path) throws DigiDoc4JException {
    logger.debug("");
    Container container = new DDocContainer(path);
    try {
      if (Helper.isZipFile(new File(path))) {
        if ("asics".equalsIgnoreCase(FilenameUtils.getExtension(path))) {
          container = new ASiCSContainer(path);
        } else
          container = new BDocContainer(path);
      }
      logger.info("Opens container " + path + " as " + container.getDocumentType());
      return container;
    } catch (IOException e) {
      DigiDoc4JException exception = new DigiDoc4JException(10, "Empty or unreadable input file");
      logger.error(exception.toString());
      throw exception;
    }
  }

  protected Container() {
  }

  /**
   * Document types
   */
  public enum DocumentType {
    /**
     * BDOC 2.1 container with mime-type "application/vnd.etsi.asic-e+zip"
     */
    ASIC_E,
    /**
     * ASiC-S container with mime-type "application/vnd.etsi.asic-e+zip"
     */
    ASIC_S,
    /**
     * DIGIDOC-XML 1.3 container
     */
    DDOC
  }

  /**
   * Signature profile format.
   */
  public enum SignatureProfile {
    /**
     * Time-mark.
     */
    TM,
    /**
     * Time-stamp.
     */
    TS,
    /**
     * no profile
     */
    NONE
  }

  /**
   * Digest algorithm
   */
  public enum DigestAlgorithm {
    SHA1,
    SHA224,
    SHA256,
    SHA512
  }

  /**
   * Adds a data file from the file system to the container.
   * <p>
   * Note:
   * Data files can be removed from a container only after all signatures have been removed.
   * </p>
   *
   * @param path     data file to be added to the container
   * @param mimeType MIME type of the data file, for example 'text/plain' or 'application/msword'
   */
  public abstract void addDataFile(String path, String mimeType);

  /**
   * Adds a data file from the input stream (i.e. the date file content can be read from the internal memory buffer).
   * <p>
   * Note:
   * Data files can be added to a container only after all signatures have been removed.
   * </p>
   *
   * @param is       input stream from where data is read
   * @param fileName data file name in the container
   * @param mimeType MIME type of the data file, for example 'text/plain' or 'application/msword'
   */
  public abstract void addDataFile(InputStream is, String fileName, String mimeType);

  /**
   * Adds a signature to the container.
   *
   * @param signature signature to be added to the container
   */
  public abstract void addRawSignature(byte[] signature);

  /**
   * Adds signature from the input stream to the container.
   *
   * @param signatureStream signature to be added to the container
   */
  public abstract void addRawSignature(InputStream signatureStream);

  /**
   * Returns all data files in the container.
   *
   * @return list of all the data files in the container.
   */
  public abstract List<DataFile> getDataFiles();

  /**
   * Removes a data file from the container by data file name. Any corresponding signatures will be deleted.
   *
   * @param fileName name of the data file to be removed
   */
  public abstract void removeDataFile(String fileName);

  /**
   * Removes the signature with the given signature id from the container.
   *
   * @param signatureId id of the signature to be removed
   */
  public abstract void removeSignature(int signatureId);

  /**
   * Saves the container to the specified location.
   *
   * @param path file name and path.
   */
  public abstract void save(String path);

  /**
   * Signs all data files in the container.
   *
   * @param signer signer implementation
   * @return signature
   */
  public abstract Signature sign(Signer signer);

  /**
   * Sets configuration for container
   *
   * @param conf configuration
   */
  public abstract void setConfiguration(Configuration conf);

  /**
   * Returns a list of all signatures in the container.
   *
   * @return list of all signatures
   */
  public abstract List<Signature> getSignatures();

  /**
   * Returns document type ASiC or DDOC
   *
   * @return document type
   */
  public abstract DocumentType getDocumentType();

  //--- differences with CPP library

  /**
   * Sets container digest type
   *
   * @param algorithm digest algorithm
   */
  public abstract void setDigestAlgorithm(DigestAlgorithm algorithm);

  /**
   * Validate container
   *
   * @return List of errors
   */
  public abstract List<DigiDoc4JException> validate();
}







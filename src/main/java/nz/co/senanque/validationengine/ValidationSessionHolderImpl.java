/**
 * 
 */
package nz.co.senanque.validationengine;

/**
 * @author roger
 *
 */
public class ValidationSessionHolderImpl implements ValidationSessionHolder {
	
	private final ValidationEngine m_validationEngine;
	private ValidationSession m_validationSession;

	public ValidationSessionHolderImpl(ValidationEngine validationEngine) {
		m_validationEngine = validationEngine;
	}
	
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#bind(java.lang.Object)
	 */
	@Override
	public void bind(Object context) {
		if (m_validationEngine != null && context instanceof ValidationObject) {
	        m_validationSession = m_validationEngine.createSession();
	        m_validationSession.bind((ValidationObject) context);
		}
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#unbind(java.lang.Object)
	 */
	@Override
	public void unbind(Object context) {
		if (m_validationSession != null && context instanceof ValidationObject) {
	        m_validationSession.unbind((ValidationObject) context);
		}
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#close()
	 */
	@Override
	public void close() {
		if (m_validationSession != null) {
	        m_validationSession.close();
		}
		m_validationSession = null;
	}

}

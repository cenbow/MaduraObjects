package nz.co.senanque.validationengine;

public interface ValidationSessionHolder {

	public abstract void bind(Object context);

	public abstract void unbind(Object context);

	public abstract void close();

}
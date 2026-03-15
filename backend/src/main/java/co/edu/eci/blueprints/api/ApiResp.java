package co.edu.eci.blueprints.api;

/**
 * Generic API response wrapper for uniform responses.
 * @param <T> The type of the response data
 */
public record ApiResp<T>(int code, String message, T data) {}

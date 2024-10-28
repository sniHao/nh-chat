package nh.chat.limiter;


public class RateLimitException extends RuntimeException{
    public RateLimitException(String message) {
        super(message);
    }
}

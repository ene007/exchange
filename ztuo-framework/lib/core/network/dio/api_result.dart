    import '../error_handler/error_handler.dart';
     
    
class ApiResult<T> {
  final T? data;
  final ErrorHandler? errorHandler;
  final bool isSuccess;

  ApiResult.success(this.data)
      : errorHandler = null,
        isSuccess = true;

  ApiResult.failure(this.errorHandler)
      : data = null,
        isSuccess = false;

  // Manually implementing the `when` method
  TResult when<TResult>({
    required TResult Function(T data) success,
    required TResult Function(ErrorHandler errorHandler) failure,
  }) {
    if (isSuccess && data != null) {
      return success(data as T);
    } else if (!isSuccess && errorHandler != null) {
      return failure(errorHandler!);
    } else {
      throw Exception("Invalid ApiResult state");
    }
  }
} 




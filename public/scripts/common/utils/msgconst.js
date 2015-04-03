'use strict';

angular.module('concordchurchApp')
  .value('msgconst', {
	httpStatus_400: {
		code: "BAD_REQUEST",
		message: "You send a Bad Request. send the right thing."
	},
	httpStatus_401: {
		code: "UNAUTHORIZED",
		message: "Login Required. Or Your login info is expired."
	},
	httpStatus_403: {
		code: "FORBIDDEN",
		message: "Your Authorized is forbidden. Request the autorization to admin."
	},
	httpStatus_404: {
		code: "NOT FOUND",
		message: "Not found the content."
	},
	httpStatus_500: {
		code: "SERVER ERROR",
		message: "Internal Server Error"
	}
});

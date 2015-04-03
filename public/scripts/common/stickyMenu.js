(function(){
    $.fn.fHeaderMenu = function( options ) {
        $(this).addClass('fHeaderMenu');
        return $('.fHeaderMenu a').each( function() {
            // get initial top offset for the menu 
            var stickyTop = $('.fHeaderMenu').offset().top;  

            var stickyMenu = function(){
                var scrollTop = $(window).scrollTop(); 
                $('.fHeaderMenu').css({ 'position': 'fixed', 'top':0 }).addClass('fxd');
                //if (scrollTop > stickyTop) { 
	            //    $('.fHeaderMenu').css({ 'position': 'fixed', 'top':0 }).addClass('fxd');
                //} else {
                //    $('.fHeaderMenu').css({ 'position': 'absolute', 'top':stickyTop }).removeClass('fxd'); 
                //}   
            };

            stickyMenu();

            $(window).scroll(function() {
                 stickyMenu();
            });
        });
	}
        
    $.fn.fFooterMenu = function( options ) {
        $(this).addClass('fFooterMenu');
        return $('.fFooterMenu a').each( function() {
            var stickyMenu = function(){
                var ht = $(window).height() - options.height;
                $('.fFooterMenu').css({ 'position': 'fixed', 'top':ht }).addClass('fxd');
            };

            stickyMenu();

            $(window).scroll(function() {
                 stickyMenu();
            });
        });
    }
       
})();
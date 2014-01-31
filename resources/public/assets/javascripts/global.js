jQuery(document).ready(function($) {
  $('.toggle-competencies').on('click', function (event) {
    $(this).parent().prev().slideToggle(250);
    if ($(this).find('.toggle-name').text() == 'Show') {
      $(this).find('.toggle-name').text('Hide');
    } else {
      $(this).find('.toggle-name').text('Show');
    }

    event.preventDefault();
  });

  $('#search-control').on('click', function (event) {
    var body = $('body'),
        searchControl = $(this),
        searchForm    = $('#search');

    if (body.attr('data-search-active') == "true") {
      body.attr('data-search-active', "false");
      searchControl.attr('data-active', "false");
      searchForm.attr('data-active', "false");
    } else {
      body.attr('data-search-active', "true");
      searchControl.attr('data-active', "true");
      searchForm.attr('data-active', "true");
    }
    event.preventDefault();
  });

  $('#side-nav-control').on('click', function (event) {
    var sideNavControl = $(this),
        sideNavBlock    = $('#side-nav');

    if (sideNavControl.attr('data-active') == "true") {
      sideNavControl.attr('data-active', "false");
      sideNavBlock.attr('data-active', "false");
    } else {
      sideNavControl.attr('data-active', "true");
      sideNavBlock.attr('data-active', "true");
    }
    event.preventDefault();
  });

  $('.result-details').find('.show-children').on('click', function(event){
    var childrenItems = $(this).parent().next();
    childrenItems.toggle();
    event.preventDefault();
  });
});

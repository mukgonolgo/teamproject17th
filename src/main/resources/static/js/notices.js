const page_elements = document.getElementsByClassName("page-link");
Array.from(page_elements).forEach(function(element){
	element.addEventListener("click",function(){
		document.getElementById("page").value=this.dataset.page;
		document.getElementById("searchForm").submit();
	})
})

const btn_search = document.getElementById("btn_search");
btn_search.addEventListener("click",function(){
	document.getElementById("kw").value= document.getElementById("search_kw").value;
	document.getElementById("page").value=0;
	//검색버튼 클릭시 페이징의 0부터 보여주기
	document.getElementById("searchForm").submit();
})
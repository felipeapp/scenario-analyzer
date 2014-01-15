<c:if test="${!ajaxScripts}">
    <script type="text/javascript" src="/shared/javascript/prototype.js"></script>
    <script type="text/javascript">
        JAWR.loader.script('/bundles/js/scriptaculous.js');
		JAWR.loader.script('/javascript/ajaxtags/overlibmws.js');
		JAWR.loader.script('/javascript/ajaxtags/ajaxtags.js');
		JAWR.loader.style('/javascript/ajaxtags/css/ajaxtags.css');
     </script>
	<c:set var="ajaxScripts" value="true"/>
</c:if>
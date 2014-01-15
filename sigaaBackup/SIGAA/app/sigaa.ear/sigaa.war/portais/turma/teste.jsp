<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>


 <t:div id="subnavigation_outer">
    <t:div id="subnavigation">
<t:panelNavigation2 id="nav1" layout="list" itemClass="mypage" activeItemClass="selected"
                         disabledStyle="color:red;padding: 2px 20px 2px 25px">
	<t:commandNavigation2 value="Item 1">
		<t:commandNavigation2 value="SubItem 1.1">
			<f:verbatim>sdksjdskjd</f:verbatim>
		</t:commandNavigation2>
	</t:commandNavigation2>
</t:panelNavigation2>

</t:div>
</t:div>



</f:view>
<script type="text/javascript" src="/shared/javascript/turma.js"> </script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
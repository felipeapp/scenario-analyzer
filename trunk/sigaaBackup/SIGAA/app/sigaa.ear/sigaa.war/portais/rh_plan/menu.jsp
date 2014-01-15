<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<input type="hidden" name="aba" id="aba"/>
	
	<h2>Portal da Reitoria</h2>
	
	<div id="operacoes-subsistema" class="reduzido">
		<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">	
			<div id="graduacao" class="aba">
				<%@include file="/portais/rh_plan/abas/graduacao.jsp"%>
			</div>
		</ufrn:checkRole>	
		<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO,
			SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO }  %>">	
			<div id="pos-graduacao" class="aba">
				<%@include file="/portais/rh_plan/abas/pos-graduacao.jsp"%>
			</div>
		</ufrn:checkRole>
		<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">	
			<div id="pesquisa" class="aba">
				<%@include file="/portais/rh_plan/abas/pesquisa.jsp"%>
			</div>
			<div id="monitoria" class="aba">
				<%@include file="/portais/rh_plan/abas/monitoria.jsp"%>
			</div>
			<div id="extensao" class="aba">
				<%@include file="/portais/rh_plan/abas/extensao.jsp"%>
			</div>		
			<div id="producao_docente" class="aba">
				<%@include file="/portais/rh_plan/abas/producao_docente.jsp"%>
			</div>
			<div id="planejamento" class="aba">
				<%@include file="/portais/rh_plan/abas/planejamento.jsp"%>
			</div>
		</ufrn:checkRole>	
	</div>
	
	<script>
	var Abas = function() {
		return {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
		        <ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
		        abas.addTab('graduacao', "Graduação");
		        </ufrn:checkRole>
		        <ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO,
					SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO }  %>">	
		        abas.addTab('pos-graduacao', "Pós-Graduação");
		        </ufrn:checkRole>
		        <ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">	
		        abas.addTab('pesquisa', "Pesquisa");
		        abas.addTab('monitoria', "Monitoria");
		        abas.addTab('extensao', "Extensão");
		        abas.addTab('producao_docente', "Produção Docente");
		        abas.addTab('planejamento', "Planejamento");
		    	</ufrn:checkRole>	
		        <c:if test="${empty sessionScope.aba}">
			        <ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">    
			        	abas.activate('graduacao');
			        </ufrn:checkRole>	
			        <ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.ACOMPANHA_DESEMPENHO_SERVIDORES_NA_POSGRADUACAO }  %>">	
						abas.activate('pos-graduacao');		
					</ufrn:checkRole>				
				    </c:if>
			    <c:if test="${sessionScope.aba != null}">
			    	abas.activate('${sessionScope.aba}');
			    </c:if>
		    }
	    }
	}();
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
	</script>
	
	</h:form>
</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
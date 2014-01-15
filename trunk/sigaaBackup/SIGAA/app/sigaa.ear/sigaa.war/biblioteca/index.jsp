<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>



<f:view>
	
	<h:form id="form">
	
	<h2> Biblioteca </h2>
	
		<%--  Remove da sess�o os MBean que n�o foi poss�vel colocar em request                --%>
		<%--  toda vez que o usuario voltar a pagina principal da biblioteca                   --%>
		<%@include file="/biblioteca/limpaAtributosSessao.jsp"%>  
	 
	 	<%-- Os dois itens abaixo s�o previstos e gerenciados por ViewFilter.java  --%>
		<input type="hidden" name="aba" id="aba" />
		<input type="hidden" name="subAba" id="subAba"  />

		<div id="operacoes-subsistema" class="reduzido">
			
			<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA }  %>">	
				<div id="administracao" class="aba">
						<%@include file="/biblioteca/menus/administracao.jsp"%>
				</div>
			</ufrn:checkRole>
			
			<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT,
				SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO, SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO}  %>"> 
			
					<div id="cadastros" class="aba">
						<%@include file="/biblioteca/menus/cadastros.jsp"%>
					</div>
				
				
					<div id="processos_tecnicos" class="aba">
						<%@include file="/biblioteca/menus/processos_tecnicos.jsp"%>
					</div>
			
			</ufrn:checkRole>  
			
			<ufrn:checkRole papeis="<%= new int[] { 
					  SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO
					, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<div id="aquisicoes" class="aba">
					<%@include file="/biblioteca/menus/aquisicoes.jsp"%>
				</div>
			</ufrn:checkRole>
			
			<ufrn:checkRole papeis="<%= new int[] { 
					  SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
					, SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO
					, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<div id="circulacao" class="aba">
					<%@include file="/biblioteca/menus/circulacao.jsp"%>
				</div>
			</ufrn:checkRole>
			
			<%-- OBSERVA��O: O bibliotec�rio de cataloga��o deve acessar essa aba atender as solicita��es de cataloga��o na fonte --%>
			
			<ufrn:checkRole papeis="<%= new int[] { 
					  SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
					, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					
				<div id="informacao_referencia" class="aba">
					<%@include file="/biblioteca/menus/informacao_referencia.jsp"%>
				</div>
			</ufrn:checkRole>
			
			
			<ufrn:checkRole papeis="<%= new int[] { 
					  SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO
					, SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO
					, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<div id="intercambio" class="aba">
					<%@include file="/biblioteca/menus/intercambio.jsp"%>
				</div>
			</ufrn:checkRole> 
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
				<div id="relatorios" class="aba">
					<%@include file="/biblioteca/menus/controle_estatistico.jsp"%>
				</div>
			</ufrn:checkRole>
			
			<c:if test="${sessionScope.usuario.servidor != null}">
				<div id="modulo_servidor" class="aba">
					<%@include file="/biblioteca/menus/modulo_biblioteca_servidor.jsp"%>
				</div>
			</c:if> 
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_GESTOR_BDTD, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<div id="teses_dissertacoes" class="aba">
					<%@include file="/biblioteca/menus/teses_dissertacoes.jsp"%>
				</div>				
			</ufrn:checkRole>
		</div> 
	
	</h:form>
	
	<c:set var="hideSubsistema" value="true" />

</f:view>

<div class="linkRodape"><html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
	var Abas = {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');

	        <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA } %>">
				abas.addTab('administracao', "Administra��o");
			</ufrn:checkRole>
			
	        <ufrn:checkRole papeis="<%= new int[] {
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
				SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO,
				SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO}  %>">
	    	
		        abas.addTab('cadastros', "Cadastros");
		        abas.addTab('processos_tecnicos', "Processos T�cnicos");
	
			</ufrn:checkRole>
	        	
	        	
			<ufrn:checkRole papeis="<%= new int[] { 
										  SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL } %>">
	        		abas.addTab('aquisicoes', "Aquisi��es");
			</ufrn:checkRole>
	
			<ufrn:checkRole papeis="<%= new int[] { 
						  SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
						, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
						, SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO
						, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	        		abas.addTab('circulacao', "Circula��o");
			</ufrn:checkRole>
	        
			// OBSERVA��O: O bibliotec�rio de cataloga��o deve acessar essa aba atender as solicita��es de cataloga��o na fonte 
	        <ufrn:checkRole papeis="<%= new int[] { 
	        			  SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
	        			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
	        			, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
						, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	        		abas.addTab('informacao_referencia', "Inf. e Refer�ncia"); 
			</ufrn:checkRole>
	        	
	        	<ufrn:checkRole papeis="<%= new int[] { 
	        			  SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO
	        			, SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO
	        			, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	        		abas.addTab('intercambio', "Interc�mbio");
			</ufrn:checkRole>
	        	
	        	<ufrn:checkRole papeis="<%= new int[] { 
	        			SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO
	        			, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
	        		abas.addTab('relatorios', "Relat�rios");
			</ufrn:checkRole>
	
			<c:if test="${sessionScope.usuario.servidor != null}">
				abas.addTab('modulo_servidor', "M�dulo do Servidor");
			</c:if>
	
			<ufrn:checkRole papeis="<%= new int[] { 
					  SigaaPapeis.BIBLIOTECA_GESTOR_BDTD
					, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				abas.addTab('teses_dissertacoes', "Teses e Disserta��es");
			</ufrn:checkRole>		
	
			
			////////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////                Ativa��o das abas          ////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////
			
			// OBS.: Vari�vel "naoEntrandoPeloMenuBiblioteca", configurada em EntrarBibliotecaVindoOutrosSistemasMBean.java
			
			/////////////////////////////////////////////////////
			// Entrou pelo sigaa no m�dulo da biblioteca
			/////////////////////////////////////////////////////
			<c:if test="${sessionScope.naoEntrandoPeloMenuBiblioteca == null}">
			
					// Se �  servidor
					<c:if test="${sessionScope.usuario.servidor != null}">
						abas.activate('modulo_servidor');
					</c:if>
					
					//Se tem o papel de emitir a quita��o, seta essa como ativa
					<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO}  %>">
						abas.activate('circulacao');
					</ufrn:checkRole>
					
					// Se possui algum outro papel na biblioteca
					<ufrn:checkRole papeis="<%= new int[] {
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
						SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO,
						SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO
						}  %>">
				
						abas.activate('cadastros');
	
					</ufrn:checkRole>
					
			</c:if>
	
			
			///////////////////////////////////////////////////////////////////////
			// Se veio de outro sistema redirecionado para o sigaa 
			///////////////////////////////////////////////////////////////////////
			
			<c:if test="${sessionScope.naoEntrandoPeloMenuBiblioteca != null}">
	
				//Se � servidor
				<c:if test="${sessionScope.usuario.servidor != null}">
					abas.activate('modulo_servidor');
				</c:if>
				
				//Se tem o papel de emitir a quita��o, seta essa como ativa
				<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO}  %>">
					abas.activate('circulacao');
				</ufrn:checkRole>
				
			</c:if>
	
			
			
	
			<%-- Ativa a aba utilizado no momento pelo usu�rio --%>	
			<c:if test="${sessionScope.aba != null && sessionScope.aba != ''}">
		    		abas.activate('${sessionScope.aba}');
		    </c:if>
	    }
	};
	
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
	
</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
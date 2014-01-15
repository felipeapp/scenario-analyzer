<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>Gest�o do Espa�o F�sico</h2>

<h:form>
	<input type="hidden" name="aba" id="aba"/>
	<div id="operacoes-subsistema"  class="reduzido">
		<div id="principal" class="aba">
			<ul>
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_ESPACO_FISICO }  %>">
				<li> Espa�os F�sicos
					<ul>
						<li> <h:commandLink value="Cadastrar" action="#{espacoFisicoBean.iniciarCadastroEspacoFisico}"></h:commandLink> </li>
						<li> <h:commandLink value="Alterar/Remover" action="#{espacoFisicoBean.iniciarBuscaEspacoFisico}"></h:commandLink> </li>
					</ul>
				</li>

				<li> Gestores de Espa�os F�sicos
					<ul>
						<li> <h:commandLink value="Cadastrar" action="#{gestorEspacoBean.iniciarCadastrar}"></h:commandLink> </li>
						<li> <h:commandLink value=" Alterar/Remover" action="#{gestorEspacoBean.iniciarBusca}"></h:commandLink> </li>
					</ul>
				</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.RESPONSAVEL_RESERVA_ESPACO }  %>">
				<li> Reserva Espa�os
					<ul>
						<li> <h:commandLink value="Gerenciar" action="#{reservaEspacoFisico.iniciarBusca}"></h:commandLink> </li>
					</ul>
				</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_ESPACO_FISICO }  %>">				
				<li> Turmas
					<ul>
						<li> <h:commandLink value="Atribuir" action="#{alocarTurmaBean.iniciar}"></h:commandLink> </li>
						<li> Visualizar Mapa de Aloca��o </li>
					</ul>
				</li>
				

				<li> Tipos de Recursos para Espa�os F�sicos
					<ul>
						<li> <h:commandLink value="Cadastrar" action="#{tipoRecursoEspacoFisicoMBean.preCadastrar}"></h:commandLink> </li>
						<li> <h:commandLink value="Alterar/Remover" action="#{tipoRecursoEspacoFisicoMBean.listar}"></h:commandLink> </li>
					</ul>
				</li>
				<li> Tipos de Espa�os F�sicos
					<ul>
						<li> <h:commandLink value="Cadastrar" action="#{tipoEspacoFisicoMBean.preCadastrar}"></h:commandLink> </li>
						<li> <h:commandLink value="Alterar/Remover" action="#{tipoEspacoFisicoMBean.listar}"></h:commandLink> </li>
					</ul>
				</li>
				</ufrn:checkRole>
			</ul>
		</div>
	</div>
</h:form>

</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	abas.addTab('principal', "Espa�o F�sico");
			abas.activate('principal');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('principal').value = aba;
}
</script>


<c:set var="hideSubsistema" value="true" />
<c:remove var="aba" scope="session"/>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
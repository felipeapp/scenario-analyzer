<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="grupoPublicoAlvo"/>

	<h2><ufrn:subSistema /> > Grupo de Público Alvo</h2>

	<h:form id="form">
	<input type="hidden" name="id" value="${grupoPublicoAlvo.obj.id}"/> 
	
	<center>
		<h:messages showDetail="true" showSummary="true"/>
		<div class="infoAltRem" style="text-align: left; width: 100%">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	 			<h:commandLink value="Listar" action="#{grupoPublicoAlvo.listar}" id="listarPublico"/>
		</div>
	</center>

	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Grupo de Público Alvo</caption>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="50" maxlength="50"
					readonly="#{grupoPublicoAlvo.readOnly}"  value="#{grupoPublicoAlvo.obj.descricao}" id="descricao"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{grupoPublicoAlvo.confirmButton}" action="#{grupoPublicoAlvo.cadastrar}" id="btCadastrar" rendered="#{grupoPublicoAlvo.confirmButton != 'Remover'}"/> 
						<h:commandButton value="#{grupoPublicoAlvo.confirmButton}" action="#{grupoPublicoAlvo.inativar}" id="btInativar" rendered="#{grupoPublicoAlvo.confirmButton == 'Remover'}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoPublicoAlvo.cancelar}" id="btCancelar"/>
					</td>
				</tr>
			</tfoot>
			
	</table>
	<br/>
	<div class="obrigatorio">Campo de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
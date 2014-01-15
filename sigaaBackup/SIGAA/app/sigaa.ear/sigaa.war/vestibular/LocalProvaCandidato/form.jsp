<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Defini��o de Locais de Provas dos Candidtos</h2>

	<div class="descricaoOperacao">
	<p><b>Caro usu�rio,</b></p>
	<p>Voc� poder� informar os locais de prova dos candidatos do vestibular utilizando 
	o formul�rio abaixo. Para tanto, voc� dever� informar:
	<ul>
		<li>O Processo Seletivo desejado.</li>
		<li>Dados para Importa��o: voc� dever� informar uma lista de dados no seguinte formato:
		 {id_local_prova, numero_inscri��o, turma}, {id_local_prova, numero_inscri��o, turma}, ... </li>
	</ul></p>
	</div>
	<h:form id="form">
		<a4j:keepAlive beanName="importaLocalProvaCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Valida��o</caption>
			<tr>
				<th class="obrigatorio">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="processoSeletivo"
						value="#{importaLocalProvaCandidatoBean.obj.processoSeletivo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Dados para Importa��o:</th>
				<td>
					<h:inputTextarea id="dadosImportacao" value="#{importaLocalProvaCandidatoBean.dadosImportacao}" rows="4" cols="60" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Validar Dados" action="#{importaLocalProvaCandidatoBean.validaDadosImportacao}" id="validaDadosImportacao"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{importaLocalProvaCandidatoBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
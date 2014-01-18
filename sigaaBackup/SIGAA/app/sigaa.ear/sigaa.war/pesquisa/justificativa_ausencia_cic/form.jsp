<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Justificativa de Aus�ncia</h2>

	<div class="descricaoOperacao" align="center">
		Informe abaixo o motivo da aus�ncia no Congresso de Inicia��o Cientifica. 
		A justificativa s� dever� ser realizada no caso de nenhum participante puder comparecer a apresenta��o. 
		Se o autor for se ausentar, mas o co-autor comparecer, ent�o n�o � necess�rio justificar.</div>

	<h:form id="form" enctype="multipart/form-data">
	<a4j:keepAlive beanName="justificativaCIC" />
	<table class="formulario"  style="align:right; width:65%">
		<caption>Dados</caption>
		<tbody>
				<tr>
				<th style=" font-weight:bold;">Edi�a� do Congresso:</th>
					<td>${justificativaCIC.obj.CIC.edicao}</td>
				</tr>
				<tr>
					<th style=" font-weight:bold;">Nome: </th>
					<td>${justificativaCIC.obj.pessoa.nomeOficial}</td>
				</tr>

			<tr>
			<th  style=" font-weight:bold;" class="required">
					Justificativa da Aus�ncia: </th>
				<td>
					<h:inputTextarea value="#{justificativaCIC.obj.justificativa}"  id="observacao" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacao_count').value = 1000 - this.value.length;" 
						onkeydown="if (this.value.length > 1000) this.value = this.value.substring(0, 1000); $('form:observacao_count').value = 1000 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Voc� pode digitar <h:inputText id="observacao_count" size="3" value="#{1000 - fn:length(buscaRegistroDiploma.observacao.observacao)}" disabled="true" /> caracteres.
				</td>
			</tr>
			
			
			<tr>			
				<th  style=" font-weight:bold;">Arquivo:</th>
		
				<td>
					<t:inputFileUpload value="#{justificativaCIC.arquivo}" style="width:95%;"/>
				</td>
			</tr>

			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar"	action="#{justificativaCIC.cadastrar}" />					
						<h:commandButton value="Cancelar" action="#{justificativaCIC.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

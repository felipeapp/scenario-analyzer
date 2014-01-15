<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
    <a4j:keepAlive beanName="termoPublicacaoTD"/>
	<h:form id="form">

		<h2><ufrn:subSistema /> > Publicação de Teste e Dissertação</h2>

		<table class="formulario">
			<caption>Informe os dados para Publicação</caption>
			<tr>
				<td colspan="2" class="subFormulario">Identificação do Autor</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<table width="100%" class="visualizacao">
						<tr>
							<th width="15%">Discente:</th>
							<td colspan="5">
								<h:outputText value="#{termoPublicacaoTD.obj.discente.matriculaNome}" />
							</td>
						</tr>
						<tr>
							<th>CPF:</th>
							<td colspan="5"><ufrn:format type="cpf_cnpj"
								valor="${termoPublicacaoTD.obj.discente.pessoa.cpf_cnpj}" /></td>
						</tr>
						<tr>
							<th>E-mail:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.email}" /></td>
						</tr>
						<tr>
							<th>Telefone:</th>
							<td colspan="5"><h:outputText
								value="#{termoPublicacaoTD.obj.discente.pessoa.telefone}" /></td>
						</tr>					
					</table>
				</td>			
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Dados da Publicação</td>
			</tr>			
			<tr>
				<th class="obrigatorio">Data na Publicação da BDTD:</th>
				<td>
					<t:inputCalendar
						value="#{termoPublicacaoTD.obj.dataPublicacaoBDTD}" size="10"
						maxlength="10" renderAsPopup="true" popupDateFormat="dd/MM/yyyy"
						renderPopupButtonAsImage="true"
						onkeypress="return formataData(this, event)" id="dataPublicBDTD">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>	
			<tr>
				<th class="obrigatorio">URL na BDTD:</th>
				<td>
					<h:inputText value="#{termoPublicacaoTD.obj.urlBDTD}" size="100" maxlength="200" id="urlBDTD"/>
					<ufrn:help img="/img/ajuda.gif">Informe o Endereço onde se encontra o trabalho na BDTD.</ufrn:help>									
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="6">
						<h:commandButton action="#{termoPublicacaoTD.cadastrar}" value="Publicar" id="botaoDePublicar"/> 
						<h:commandButton action="#{termoPublicacaoTD.cancelar}" onclick="#{confirm}" value="Cancelar" id="botaoDeCancalOperation"/>
					</td>
				</tr>
			</tfoot>					
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
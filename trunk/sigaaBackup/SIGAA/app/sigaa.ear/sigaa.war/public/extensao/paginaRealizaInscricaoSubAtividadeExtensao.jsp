<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@taglib uri="/tags/ufrn" prefix="ufrn"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.InscricaoAtividade"%>

<style type="text/css">
	strong { font-weight: bold; }
</style>

<%@ taglib uri="/tags/struts-html" prefix="html"%>

	
	<h2>Inscreve-se no curso ou evento de extens�o selecionado</h2>
	
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Abaixo o formul�rio de inscri��o no 
		 ${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.subAtividade.tipoSubAtividadeExtensao.descricao}
		 <b><i>${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.subAtividade.titulo}</i></b>
		 </p>
	</div>
	
	<h:form id="formRealizaInscricaoCursoEventoExtensao" enctype="multipart/form-data">
	   
	   		
			<table class="formulario" style="width: 100%;">
				<caption>Formul�rio de Inscri��o</caption>
				
				<tr>
					<td colspan="4" class="subFormulario">Dados da Mini Atividade</td>
				</tr>
				<tr>
					<td colspan="4" style="font-weight: bold;">
						${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.subAtividade.titulo}
					</td>
				</tr>
				<tr>
					<th>Coordenador:</th>
					<td>
						${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.subAtividade.atividade.coordenacao.pessoa.nome}
					</td>
					<th>Vagas Restantes:</th>
					<td style="font-weight: bold; ${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.quantidadeVagasRestantes <= 0 ? 'color: red;' : ''}">
						${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.quantidadeVagasRestantes}
					</td>
				</tr>
				<tr>
					<td colspan="4" style="color: green; font-weight: bold; text-align:center;">
						<c:if test="${inscricaoParticipanteAtividadeMBean.obj.inscricaoAtividade.metodoDePreenchimentoComConfirmacao
							|| inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.cobrancaTaxaMatricula}">
							Essa inscri��o exige a confirma��o do coordenador para que seja efetivada
						</c:if>
					</td>
				</tr>
				
				<tr>
					<td colspan="4" class="subFormulario">Instru��es</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="descricaoOperacao" style="width: 80%; ">
							<h:outputText value="#{inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.instrucoesInscricao}" escape="false"/>
						</div>
					</td>
				</tr>
				
				<tr>
					<td colspan="4" class="subFormulario">V�nculo</td>
				</tr>
				<tr>
					<th class="obrigatorio">Institui��o:</th>
					<td colspan="3">
						<h:inputText id="instituicao" value="#{inscricaoParticipanteMiniAtividadeMBean.obj.instituicao}" size="55" maxlength="60" />
					</td>
				</tr>
				
				
				<c:if test="${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.cobrancaTaxaMatricula}">
					<tr>
						<td colspan="4" class="subFormulario">Taxa de Matr�cula</td>
					</tr>
					<tr>
						<td colspan="4">
							<div class="descricaoOperacao" style="width: 70%; ">
								<p><strong>IMPORTANTE: Antes de realizar a inscri��o esteja ciente de que em nenhuma 
								hip�tese ser� poss�vel devolver o valor pago pelo curso ou evento.</strong> </p>
								<p style="text-align: center;">
									<h:selectBooleanCheckbox value="#{inscricaoParticipanteMiniAtividadeMBean.usuarioConcordaCondicoesPagamento}" /> Concordo com os termos acima descritos
								</p>
							</div>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Modalidade de Participa��o:</th>
						<td>
							<h:selectOneMenu id="comboModalidades"  value="#{inscricaoParticipanteMiniAtividadeMBean.idMolidadeParticipanteSelecionada}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
								<f:selectItems value="#{inscricaoParticipanteMiniAtividadeMBean.modalidadeParticipantesDoPeriodoInscricao}" />
								<a4j:support event="onchange" actionListener="#{inscricaoParticipanteMiniAtividadeMBean.atualizarModalidadeEscolhida}" reRender="divValorASerPago" />
							</h:selectOneMenu>
							<div>
							<b>Aten��o:</b> pode ser exigido algum documento que comprove essa informa��o.
							</div>
						</td>	
					</tr>
					
					<tr>
						<td colspan="4">
							<t:div id="divValorASerPago">
								<table>
									<tr>
										
										<th style="width: 30%;">Data Limite do Pagamento:</th>
										<td  style="width: 20%; font-weight:bold;">
											<h:outputText value="#{inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.dataVencimentoGRU}" />
										</td>
										
										<th style="width: 30%;">Valor a ser Pago:</th>
										<td  style="width: 20%; font-weight:bold;">
											<h:outputText value="#{inscricaoParticipanteMiniAtividadeMBean.obj.valorTaxaMatriculaFormatado}" 
												rendered="#{inscricaoParticipanteMiniAtividadeMBean.idMolidadeParticipanteSelecionada > 0}"/>
											<h:outputText value="---" 
												rendered="#{inscricaoParticipanteMiniAtividadeMBean.idMolidadeParticipanteSelecionada <= 0}"/>
										</td>
										
									</tr>
								</table>
							</t:div>
						</td>
					</tr>
					
				</c:if>
				
				<tr>
					<td colspan="4" class="subFormulario">Arquivo</td>
				</tr>
				
				<c:set var="arquivoObrigatorio" value="${ inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.envioArquivoObrigatorio ? 'required' : '' }" />
				<tr>
					<th class="${ arquivoObrigatorio }"> Descri��o do Arquivo:</th>
					<td colspan="3">
						<h:inputText  id="descricao" value="#{inscricaoParticipanteMiniAtividadeMBean.obj.descricaoArquivo}" size="83" maxlength="90"/>
					</td>
				</tr>
			
				<tr>
					<th class="${ arquivoObrigatorio }">Arquivo:</th>
					<td colspan="3">
						<t:inputFileUpload id="uFile" value="#{inscricaoParticipanteMiniAtividadeMBean.obj.file}" storage="file" size="70"/>
					</td>
				</tr>
				
				<%-- SE HOUVER QUESTION�RIO, UTILIZA A P�GINA PADR�O DE PREENCHIMENTO  --%>
				<c:if test="${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.questionario != null}">
					<tr>
						<td colspan="5" height="5px"></td>
					</tr>
					<tr>
						<td colspan="6" class="subFormulario"> Question�rio para Inscri��o: ${inscricaoParticipanteMiniAtividadeMBean.obj.inscricaoAtividade.questionario.titulo} </td>
					</tr>
					<tr>
						<td colspan="6">
							<%@include file="/geral/questionario/_formulario_respostas.jsp" %>
						</td> 
					</tr>
				</c:if>
				
				<tfoot>
					<tr>
						<td colspan="4">
								
								<h:commandButton id="cmdButRealizarInscricao" value="Confirmar Inscri��o" 
									action="#{inscricaoParticipanteMiniAtividadeMBean.inscreverParticipante}" 
									onclick="return confirm('Confirma a inscri��o nessa Mini Atividade ?');" />
								
								&nbsp;&nbsp;&nbsp;
								
								<h:commandButton id="cmdButCancelar" value="Cancelar" 
									action="#{inscricaoParticipanteMiniAtividadeMBean.telaListaPeriodosInscricoesAbertosMiniAtividades}" 
									immediate="true" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
			
	</h:form>
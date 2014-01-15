<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.rich-progress-bar-width { width: 600px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Processamento do Resultado da Avalia��o Institucional</h2>

	<h:form>
	<a4j:keepAlive beanName="processamentoAvaliacaoInstitucional"></a4j:keepAlive>
	<div align="center">
		<a4j:region id="progressPanel">
			<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
				value="#{ processamentoAvaliacaoInstitucional.percentualProcessado }"
				label ="#{ processamentoAvaliacaoInstitucional.mensagemProgresso }"
				reRenderAfterComplete="progressPanel" ignoreDupResponses="true">
					<f:facet name="initial">
						<a4j:outputPanel>
							<table class="formulario" width="70%">
								<caption class="listagem">Informe o Ano e o Per�odo</caption>
								<tbody>
									<tr>
										<th class="rotulo">Formul�rio:</th>
										<td style="text-align: left;">
											${ processamentoAvaliacaoInstitucional.obj.formulario.titulo }
										</td>
									</tr>
									<tr>
										<th class="rotulo">Perfil do Entrevistado:</th>
										<td style="text-align: left;">
											${ processamentoAvaliacaoInstitucional.obj.formulario.descricaoTipoAvaliacao }
										</td>
									</tr>
									<tr>
										<th class="rotulo">Ano-Per�odo:</th>
										<td style="text-align: left;">
											${ processamentoAvaliacaoInstitucional.obj.ano }.${ processamentoAvaliacaoInstitucional.obj.periodo }
										</td>
									</tr>
									<tr>
										<th width="40%" class="required">N� M�nimo de Avalia��es por Docente:</th>
										<td style="text-align: left;">
											<h:inputText value="#{processamentoAvaliacaoInstitucional.obj.numMinAvaliacoes}" id="numMinAvaliacoes" 
											 size="3" maxlength="2" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
											 <ufrn:help>Informe um n�mero m�nimo de Avalia��es Institucionais por Docente que ser� considerado para o processamento.</ufrn:help>
										</td>
									</tr>
									<tr>
										<th class="obrigatorio">Excluir Avalia��es de Discentes que Foram Reprovados por Falta:</th>
										<td style="text-align: left;">
											<h:selectBooleanCheckbox value="#{processamentoAvaliacaoInstitucional.excluirRepovacoesFalta}" id="exportarDadosFiltrados"/>
											 <ufrn:help>Caso o discente tenha sido reprovado por falta, a sua Avalia��o Institucional ser� ignorada na exporta��o de dados.</ufrn:help>
										</td>
									</tr>
									<tr>
										<td colspan="2" class="subFormulario">Ignorar Avalia��es de Discentes que N�o Avaliaram, ou Deram Zero, �s Perguntas:</td>
									</tr>
									<tr>
										<td colspan="2">
											<div style="text-align: left;">
											<h:selectManyCheckbox value="#{processamentoAvaliacaoInstitucional.perguntasSelecionadas}" layout="pageDirection" id="perguntasSelecionadas">
												<f:selectItems value="#{processamentoAvaliacaoInstitucional.perguntasComboBox}"/>
											</h:selectManyCheckbox>
											</div>
										</td>
									</tr>
								</tbody>
								<tfoot>
									<tr>
										<td colspan="2" align="center">
											<a4j:commandButton value="Processar" action="#{processamentoAvaliacaoInstitucional.processarAvaliacao}" id="processar"  reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
											<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{processamentoAvaliacaoInstitucional.cancelar}" id="cancelar"/>
										</td>
									</tr>
								</tfoot>
							</table>
							<br/>
							<c:set var="exibirApenasSenha" value="true" scope="request"/>
							<div style="text-align: left; width: 50%" >
								<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
							</div>
						</a4j:outputPanel>
					</f:facet>
				</rich:progressBar>
			</a4j:region>
		</div>
	</h:form>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

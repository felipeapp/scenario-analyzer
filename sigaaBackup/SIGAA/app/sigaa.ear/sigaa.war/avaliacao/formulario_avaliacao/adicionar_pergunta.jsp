<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="title"><ufrn:subSistema /> &gt; Formulário de Avaliação Institucional &gt; Adicionar Perguntas</h2>

	<h:form id="form">
	
	<table class="formulario" width="80%">
		<caption>Informe os Dados para a Pergunta</caption>
		<tbody>
			<tr>
				<th class="rotulo">Grupo:</th>
				<td>${cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.grupo.titulo }</td>
			</tr>
			<tr>
				<th valign="top" class="required">Pergunta:</th>
				<td>
					<h:inputTextarea value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.descricao }" cols="80" rows="4"/>
				</td>
			</tr>
			<tr>
				<th class="required">Tipo:</th>
				<td>
					<h:selectOneMenu value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.tipoPergunta }" id="tipoPergunta">
						<f:selectItems value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.tipoPerguntaCombo}" />
						<a4j:support  event="onchange" reRender="form"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.avaliarTurmas }" id="avaliaTurmas"/></th>
				<td>Esta pergunta avalia turmas</td>
			</tr>
			<c:if test="${cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.tipoPergunta.multiplaEscolha || cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.tipoPergunta.unicaEscolha }">
				<tr>
					<td class="subFormulario" colspan="2">Alternativas à Resposta</td>
				</tr>
				<tr>
					<td colspan="2">
					<table width="100%">
						<thead>
							<tr>
								<th>Alternativa de Resposta à Pergunta</th>
								<th width="15%">Permite Citação</th>
								<th width="3%"></th>
							</tr>
						</thead>
						<c:if test="${ not empty cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.alternativas }" >
							<c:forEach items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.pergunta.alternativas }" var="alternativa" varStatus="alternativaStatus" >
								<tr>
									<td style="text-align: left;">
										${alternativa.descricao}
									</td>
									<td style="text-align: center;">
										<ufrn:format type="simNao" valor="${ alternativa.permiteCitacao }" />
									</td>
									<td style="text-align: center;">
										<h:commandLink action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.removeAlternativa }" id="removeAlternativa" onclick="#{ confirm }">
											<h:graphicImage alt="Remove Alternativa" title="Remove Alternativa" value="/img/delete.gif"/>
											<f:param name="ordem" value="#{ alternativa.ordem }"/>
										</h:commandLink>
									</td>
								</tr>
							</c:forEach>
						</c:if>
						<tr>
							<td><h:inputText value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.alternativa.descricao }" size="60" maxlength="80"/></td>
							<td style="text-align: center">
								<h:selectBooleanCheckbox value="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.alternativa.permiteCitacao }" id="permiteCitacao"
									title="Permite citação" />
							</td>
							<td colspan="2">
								<h:commandButton value="Adicionar Alternativa" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.adicionaAlternativa }"/>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="#{cadastrarFormularioAvaliacaoInstitucionalMBean.confirmButton}" id="adicionar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.adicionaPergunta}" />
					<h:commandButton value="<< Voltar" id="voltar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.formCadastro}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

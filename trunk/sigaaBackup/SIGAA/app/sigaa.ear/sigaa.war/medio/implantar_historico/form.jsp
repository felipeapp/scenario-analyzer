<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	#tableMatriculas .colLeft{text-align: left; }
	#tableMatriculas .colCenter{text-align: center; }
	#tableMatriculas .colRight{text-align: right; }
</style>

<f:view>
	<a4j:keepAlive beanName="implantarHistoricoMedioMBean"></a4j:keepAlive>
	<h2 class="title"> <ufrn:subSistema /> &gt; Implantar Histórico</h2>

	<c:set var="discente" value="#{implantarHistoricoMedioMBean.discente}" />
	<%@include file="/medio/discente/info_discente.jsp"%>		

	<table class="formulario" width="100%">
	<caption>Implantação de Histórico</caption>
	<tr><td>

		<h:form id="form">
			<table class="subFormulario" width="100%">
				<caption>Dados da Matrícula</caption>
	
				<tr>
					<th class="obrigatorio">Série:</th>
					<td>
						<a4j:region>
							<h:selectOneMenu value="#{implantarHistoricoMedioMBean.serie.id}" id="selectSerie"
								valueChangeListener="#{implantarHistoricoMedioMBean.carregarDisciplinasBySerie }" style="width: 95%">
								<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						 		<f:selectItems value="#{implantarHistoricoMedioMBean.seriesByCurso}" /> 
						 		<a4j:support event="onchange" reRender="selectDisciplina" />
							</h:selectOneMenu>
							<a4j:status>
					                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
						</a4j:region>						
					</td>
				</tr>	
				<tr>
					<th class="obrigatorio">Disciplina:</th>
					<td>
						<h:selectOneMenu value="#{ implantarHistoricoMedioMBean.obj.componente.id }" style="width: 95%;" id="selectDisciplina">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ implantarHistoricoMedioMBean.disciplinas}" />
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tr>
					<th class="obrigatorio"> ${ implantarHistoricoMedioMBean.descricaoMetodoAvaliacao }:</th>
					<td>
					
						<c:choose>
							<c:when test="${ implantarHistoricoMedioMBean.conceito }">
								<select name="conceito_selecionado">
									<option value="-1">-</option>
									<c:forEach var="conceito" items="${ consolidacaoIndividual.conceitos }">
										<option value="${ conceito.valor }" ${ (consolidacaoIndividual.obj.conceito == conceito.valor) ? 'selected="selected"' : '' }>${ conceito.conceito }</option>
									</c:forEach>
								</select>
							</c:when>
							<c:when test="${ implantarHistoricoMedioMBean.nota }">
								<h:inputText value="#{implantarHistoricoMedioMBean.obj.mediaFinal}" id="media" size="5" maxlength="5" onkeydown="return(formataValor(this, event, 1))">
									<f:converter converterId="convertNota"/>
								</h:inputText>
							</c:when>
							<c:when test="${ implantarHistoricoMedioMBean.competencia }">
								<h:selectOneRadio value="#{implantarHistoricoMedioMBean.obj.apto}" id="competencia">
									<f:selectItem itemLabel="Apto" itemValue="true"/>
									<f:selectItem itemLabel="Inapto" itemValue="false"/>
								</h:selectOneRadio>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano:</th>
					<td>
						<h:inputText id="txtAno" value="#{implantarHistoricoMedioMBean.obj.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Situação:</th>
					<td>
						<h:selectOneMenu value="#{implantarHistoricoMedioMBean.obj.situacaoMatricula.id}" id="selectSituacao">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{implantarHistoricoMedioMBean.situacoesMatriculas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tfoot>
					<tr>
					<td colspan="2">
						<h:commandButton value="Adicionar" action="#{implantarHistoricoMedioMBean.adicionarMatricula}" id="adicionar"/>
					</td>
					</tr>
				</tfoot>
			</table>
		</h:form>

	</td></tr>

	<tr><td>
		<div class="infoAltRem">
	    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Excluir Matrícula
		</div>
	</td></tr>

	<tr><td>

		<table class="subFormulario" width="100%">
			<caption>Matrículas Cadastradas</caption>
		
			<c:if test="${not empty implantarHistoricoMedioMBean.matriculas}">
			<tr><td>
				<h:dataTable value="#{implantarHistoricoMedioMBean.matriculasDataModel}" var="mat"
					columnClasses="colCenter, colCenter, colLeft, colLeft, colRight, colCenter, colLeft, colCenter"
					rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculas" >
							
					<h:column></h:column>
					<h:column  headerClass="colCenter">
						<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
						<h:outputText value="#{mat.ano}"/>
					</h:column>
					
					<h:column  headerClass="colLeft">
						<f:facet name="header"><f:verbatim>Série</f:verbatim></f:facet>
						<h:outputText value="#{mat.serie.descricaoCompleta}"/>
					</h:column>					
		
					<h:column  headerClass="colLeft">
						<f:facet name="header"><f:verbatim>Disciplina</f:verbatim></f:facet>
						<h:outputText value="#{mat.componente.nome}"/>
					</h:column>
		
					<h:column headerClass="colRight">
						<f:facet name="header"><f:verbatim>${ implantarHistoricoMedioMBean.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
						<h:outputText value="#{ implantarHistoricoMedioMBean.conceito ? mat.conceitoChar : implantarHistoricoMedioMBean.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
					</h:column>
					<h:column><div style="margin-right:10px"></div></h:column>
		
					<h:column >
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
					</h:column>
		
					<h:column >
						<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
						<h:form>
							<h:commandLink action="#{implantarHistoricoMedioMBean.removerMatricula}" id="remover" onclick="#{confirmDelete}">
								<f:param name="idComponente" value="#{mat.componente.id}" />
								<h:graphicImage url="/img/delete.gif" title="Excluir Matrícula" />
							</h:commandLink>
						</h:form>
					</h:column>
				</h:dataTable>
			</td></tr>
			</c:if>
		
		</table>

	</td></tr>
	
	<tfoot>
	<tr>
		<td>
			<h:form prependId="false">
				<h:commandButton value="<< voltar" action="#{implantarHistoricoMedioMBean.iniciar}" id="voltar" />
				<h:commandButton value="Cancelar" action="#{implantarHistoricoMedioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
				<h:commandButton value="Avançar >>" action="#{implantarHistoricoMedioMBean.submeterDados}" id="submeterDados" />
			</h:form>
		</td>
	</tr>
	</tfoot>
	
	</table>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
		<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
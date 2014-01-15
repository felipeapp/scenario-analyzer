<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao.js"></script>

<c:set var="confirmEdicao" value="return confirm('Deseja cancelar a edição? Todos os dados digitados serão perdidos!');" scope="application"/>

<style>
	#tableMatriculas .colLeft{text-align: left; }
	#tableMatriculas .colCenter{text-align: center; }
	#tableMatriculas .colRight{text-align: right; }
	
	#tableMatriculasAnteriores .colLeft{text-align: left; }
	#tableMatriculasAnteriores .colCenter{text-align: center; }
	#tableMatriculasAnteriores .colRight{text-align: right; }
	
	
	.colSituacao{ text-align: left; width: 25%; }
	.colAlterarRemover{ text-align: center; width: 2%; }
	.colFrequencia{ text-align: left; width: 13%; }
	.colConceito{ text-align: left; width: 10%; }
	.colComponente{ text-align: left; width: 40%; }
	.colAnoPeriodo{ text-align: left; width: 10%; }
	
</style>

<f:view>
	
	<h2 class="title"> <ufrn:subSistema /> &gt; Implantar Histórico</h2>

	<c:set var="discente" value="#{implantarHistorico.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<table class="formulario" width="100%">
	<caption>Implantação de Histórico</caption>
	<tr><td>

		<h:form id="form">
			<h:inputHidden id="idMatriculaOriginal" value="#{implantarHistorico.matricula.id}" />		
		
			<table class="subFormulario" width="100%">
				<caption>Dados da Matrícula</caption>
	
				<tr>
					<th class="required">Componente Curricular:</th>
					<td>
						<c:if test="${ implantarHistorico.matricula.id <= 0 }">
						
						<h:inputHidden id="idDisciplina" value="#{implantarHistorico.matricula.componente.id}" />
						<h:inputText id="nomeDisciplina" value="#{implantarHistorico.matricula.componente.nome}" size="80" />
						<ajax:autocomplete
								source="form:nomeDisciplina" target="form:idDisciplina"
								baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
								indicator="indicatorDisciplina" minimumCharacters="3" parameters="nivel=${implantarHistorico.nivelEnsino}"
								parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; "> <img src="/sigaa/img/indicator.gif" > </span>
						<span style="required"></span>
						</c:if>
						
						<c:if test="${ implantarHistorico.matricula.id > 0 }">
						
							${ implantarHistorico.matricula.componente.codigoNome }
						
						</c:if>
					</td>
				</tr>
	
				<tr>
					<th class="required"> ${ implantarHistorico.descricaoMetodoAvaliacao }:</th>
					<td>
					
						<c:choose>
							<c:when test="${ implantarHistorico.conceito }">
								<select name="conceito_selecionado">
									<option value="-1">-</option>
									<c:forEach var="conceito" items="${ consolidacaoIndividual.conceitos }">
										<option value="${ conceito.valor }" ${ (implantarHistorico.matricula.conceito == conceito.valor) ? 'selected="selected"' : '' }>${ conceito.conceito }</option>
									</c:forEach>
								</select>
							</c:when>
							<c:when test="${ implantarHistorico.nota }">
							<!-- maxlength="4" onkeydown="return(formataValorNota(this, event, 1))" onblur="verificaNotaMaiorDez(this)"  -->
								<h:inputText value="#{implantarHistorico.matricula.mediaFinal}" id="media" size="5" maxlength="5" 
								onkeydown="return(formataValorNota(this, event, 1))" >
									<f:converter converterId="convertNota"/>
								</h:inputText>
							</c:when>
							<c:when test="${ implantarHistorico.competencia }">
								<h:selectOneRadio value="#{implantarHistorico.matricula.apto}" id="competencia">
									<f:selectItem itemLabel="Apto" itemValue="true"/>
									<f:selectItem itemLabel="Inapto" itemValue="false"/>
								</h:selectOneRadio>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					
					</td>
				</tr>
				<c:if test="${not implantarHistorico.portalPpg and not implantarHistorico.portalCoordenadorStricto}">
					<tr>
						<th class="required">Ano-Período:</th>
						<td>
							<h:inputText id="txtAno" value="#{implantarHistorico.matricula.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> -
							<h:inputText id="txtPeriodo" value="#{implantarHistorico.matricula.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${ implantarHistorico.portalPpg or implantarHistorico.portalCoordenadorStricto }">
					<tr>
						<th class="required"> Mês/Ano inicial: </th>
						<td>
							<h:selectOneMenu value="#{implantarHistorico.matricula.mes}" id="selectMesInicio">
								<f:selectItem itemLabel=" -- Selecione" itemValue="0" /> 
								<f:selectItems value="#{implantarHistorico.meses}"/>
							</h:selectOneMenu>&nbsp;/&nbsp;
							<h:selectOneMenu value="#{implantarHistorico.matricula.ano}" onchange="$('form:ano').value = this.value;" id="selectAnoInicio">
								<f:selectItem itemLabel=" -- Selecione" itemValue="0" />
								<f:selectItems value="#{implantarHistorico.anos}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="required"> Mês/Ano Final: </th>
						<td>
							<h:selectOneMenu value="#{implantarHistorico.matricula.mesFim}" id="selectMesFinal">
								<f:selectItem itemLabel=" -- Selecione" itemValue="0" />
								<f:selectItems value="#{implantarHistorico.meses}"/>
							</h:selectOneMenu>&nbsp;/&nbsp;
							<h:selectOneMenu value="#{implantarHistorico.matricula.anoFim}" id="selectAnoFinal">
								<f:selectItem itemLabel=" -- Selecione" itemValue="0" />
								<f:selectItems value="#{implantarHistorico.anos}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				
				<c:if test="${implantarHistorico.permiteInserirFrequencia}">
					<tr>
						<th class="required">Frequencia:</th>
						<td>
							<h:inputText id="freqAluno" onkeyup="return formatarInteiro(this);" value="#{implantarHistorico.matricula.frequenciaImplantadaHistorico}" size="3" maxlength="3" />
							<ufrn:help>O valor a ser inserido se refere à PORCENTAGEM total das aulas que o aluno compareceu.</ufrn:help>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th class="required">Situação:</th>
					<td>
						<h:selectOneMenu value="#{implantarHistorico.matricula.situacaoMatricula.id}" id="selectSituacao">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{implantarHistorico.situacoesMatriculas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tfoot>
					<tr>
					<td colspan="2">
						<h:commandButton value="#{implantarHistorico.confirmButton}" action="#{implantarHistorico.adicionarMatricula}" id="adicionar"/>
						<h:commandButton value="Limpar" action="#{implantarHistorico.cancelarEdicao}" id="cancelarAdicao" onclick="#{confirmEdicao}" immediate="true" />
					</td>
					</tr>
				</tfoot>
			</table>
		</h:form>

	</td></tr>

	<tr><td>
		<div class="infoAltRem">
	    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Excluir Matrícula
	    	<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Matrícula
		</div>
	</td></tr>

	
<c:if test="${not empty implantarHistorico.matriculas}">
	
	<tr><td>

		<table class="subFormulario" width="100%">
			<caption>Matrículas Adicionadas</caption>
		
			<tr><td>
				<h:dataTable value="#{implantarHistorico.matriculasDataModel}" var="mat"
					columnClasses="colCenter, colCenter, colComponente, colRight, colRight, colSituacao, colAlterarRemover, colAlterarRemover"
					rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculas">
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Inicial"/>
							</f:facet>
							<h:outputText value="#{mat.mes}"/>/<h:outputText value="#{mat.ano}" style="text-align: center;"/>
						</h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Final"/>
							</f:facet>
							<h:outputText value="#{mat.mesFim}"/>/<h:outputText value="#{mat.anoFim}"/>
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						<h:column></h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header"><f:verbatim>Ano-Período</f:verbatim></f:facet>
							<h:outputText value="#{mat.anoPeriodo}"/>
						</h:column>
					</c:if>
		
					<h:column  headerClass="colComponente">
						<f:facet name="header"><f:verbatim>Componente</f:verbatim></f:facet>
						<h:outputText value="#{mat.componente.nome}"/>
					</h:column>
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
						
					</c:if>
					<c:if test="${not implantarHistorico.discente.stricto}">
						
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
		
					<h:column  headerClass="colRight">
						<f:facet name="header"><f:verbatim>Frequência</f:verbatim></f:facet>
						<%-- <h:outputText value="#{mat.frequenciaImplantadaHistorico}"/> --%>
						<h:outputText value="#{mat.frequenciaImplantadaHistorico}"/>
					</h:column>
					
					<h:column headerClass="colLeft">
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
					</h:column>
		
		
					<h:column >
						<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
						<h:form>
							<h:commandLink action="#{implantarHistorico.alterarImplantacaoAnterior}" id="alterarImplantacaoAlterior">
								<f:param name="idMatriculaAnterior" value="#{mat.id}" />
								<f:param name="idComponenteAnterior" value="#{mat.componente.id}" />
								<h:graphicImage url="/img/alterar.gif" title="Alterar Matrícula" />
							</h:commandLink>
						</h:form>
					</h:column>
		
					<h:column >
						<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
						<h:form>
							<h:commandLink action="#{implantarHistorico.removerMatricula}" id="remover" onclick="#{confirm}">
								<f:param name="idComponente" value="#{mat.componente.id}" />
								<h:graphicImage url="/img/delete.gif" title="Excluir Matrícula" />
							</h:commandLink>
						</h:form>
					</h:column>
				</h:dataTable>
			</td></tr>
		
		</table>

	</td></tr>
	
	
	</c:if>
	
	
	
	<c:if test="${not empty implantarHistorico.matriculasAnteriores}">
	
	 <tr><td>

		<table class="subFormulario" width="100%">
			<caption>Matrículas Implantadas Anteriormente</caption>
		
			
			<tr><td>
				<h:dataTable value="#{implantarHistorico.matriculasAnterioresDataModel}" var="mat"
					columnClasses="colCenter, colCenter, colComponente, colRight, colRight, colSituacao, colAlterarRemover,colAlterarRemover"
					rowClasses="linhaPar,linhaImpar" width="100%" id="tableMatriculasAnteriores" >
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Inicial"/>
							</f:facet>
							<h:outputText value="#{mat.mes}"/>/<h:outputText value="#{mat.ano}" style="text-align: center;"/>
						</h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header">
								<h:outputText value="Data Final"/>
							</f:facet>
							<h:outputText value="#{mat.mesFim}"/>/<h:outputText value="#{mat.anoFim}"/>
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						<h:column></h:column>
						<h:column  headerClass="colCenter">
							<f:facet name="header"><f:verbatim>Ano-Período</f:verbatim></f:facet>
							<h:outputText value="#{mat.anoPeriodo}"/>
						</h:column>
					</c:if>
		
					<h:column  headerClass="colComponente">
						<f:facet name="header"><f:verbatim>Componente</f:verbatim></f:facet>
						<h:outputText value="#{mat.componente.codigoNome}"/>
					</h:column>
		
					<c:if test="${implantarHistorico.discente.stricto}">
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
					
					<c:if test="${not implantarHistorico.discente.stricto}">
						
						<h:column headerClass="colRight">
							<f:facet name="header"><f:verbatim>${ implantarHistorico.descricaoMetodoAvaliacao }</f:verbatim></f:facet>
							<h:outputText value="#{ implantarHistorico.conceito ? mat.conceitoChar : implantarHistorico.nota ? mat.mediaFinal : mat.competenciaDescricao }" />
						</h:column>
					</c:if>
		
					<h:column headerClass="colRight">
						<f:facet name="header"><f:verbatim>Frequência</f:verbatim></f:facet>
						<%-- <h:outputText value="#{mat.frequenciaImplantadaHistorico}"/> --%>
						<h:outputText value="#{mat.frequenciaImplantadaHistorico}"/>
					</h:column>
					
					
					<h:column headerClass="colLeft" >
						<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
						<h:outputText value="#{mat.situacaoMatricula.descricao}"/>
					</h:column>
		
					<h:column >
						<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
						<h:form>
							<h:commandLink action="#{implantarHistorico.alterarImplantacaoAnterior}" id="alterarImplantacaoAlterior">
								<f:param name="idMatriculaAnterior" value="#{mat.id}" />
								<h:graphicImage url="/img/alterar.gif" title="Alterar Matrícula" />
							</h:commandLink>
						</h:form>
					</h:column>
					
					<h:column >
						<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
						<h:form>
							<h:commandLink action="#{implantarHistorico.removerImplantacaoAnterior}" id="removerImplantacaoAnterior" onclick="#{confirm}">
								<f:param name="idMatriculaAnterior" value="#{mat.id}" />
								<h:graphicImage url="/img/delete.gif" title="Excluir Matrícula" />
							</h:commandLink>
						</h:form>
					</h:column>
					
					
				</h:dataTable>
			</td></tr>
		
		</table>

	</td></tr> 
	
	</c:if>
	
	
	
	
	<tfoot>
	<tr>
		<td>
			<h:form prependId="false">
				<h:commandButton value="<< Voltar" action="#{implantarHistorico.voltarBuscaDiscente}" id="voltar" />
				<h:commandButton value="Cancelar" action="#{implantarHistorico.iniciar}" onclick="#{confirm}" immediate="true" id="cancelar" />
				<h:commandButton value="Avançar >>" action="#{implantarHistorico.submeterDados}" id="submeterDados" />
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
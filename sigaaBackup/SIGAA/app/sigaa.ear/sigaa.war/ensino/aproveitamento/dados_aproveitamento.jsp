<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.SituacaoMatricula"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR} ); %>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento de estudos &gt; Dados do aproveitamento</h2>

	<table class="visualizacao" width="80%">
	<tr>
		<th width="20%"> Discente: </th>
		<td> ${aproveitamento.obj.discente.matricula } - ${aproveitamento.obj.discente.nome }</td>
	</tr>
	<tr>
		<th> Curso: </th>
		<td> ${aproveitamento.obj.discente.curso.descricao} </td>
	</tr>
	<tr>
		<th> Ano-Período de Ingresso: </th>
		<td> ${aproveitamento.obj.discente.anoPeriodoIngresso} </td>
	</tr>
	<ufrn:subSistema teste="tecnico">
		<tr>
			<th> Turma de Entrada: </th>
			<td> ${aproveitamento.obj.discente.turmaEntradaTecnico.descricao} </td>
		</tr>
	</ufrn:subSistema>
	<ufrn:subSistema teste="graduacao">
		<tr>
			<th> Currículo: </th>
			<td> ${aproveitamento.obj.discente.curriculo.descricao} </td>
		</tr>
	</ufrn:subSistema>
	</table>
	<br />
<style>
	.menu-botoes ul li a h5 {
		left: 50px !important;
	}
	
	.menu-botoes ul li a p {
		background-image: url('/sigaa/img/graduacao/cancelar.png');
		padding-left: 40px !important;
	}
</style>	
<h:form id="form">
	<c:if test="${aproveitamento.permiteCancelarMatricula}">
		<table class="formulario" width="50%">
			<caption>Cancelamento de Matrícula</caption>
			<tr>
				<td>
					<c:set var="exibirApenasSenha" value="true"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
							
					<div class="menu-botoes" style="width: 350px; margin: 0 auto; margin-top:10px; padding-bottom: 60px;">
						<ul class="menu-interno">
							<li class="botao-grande plano;">
						    	<h:commandLink action="#{aproveitamento.cancelarMatricula}" rendered="#{aproveitamento.permiteCancelarMatricula}">
									<h5>Excluir Matrícula</h5> 
									<p>O aluno está matriculado no Componente: <b>${aproveitamento.obj.componente.codigo}</b>, Clique Aqui para Excluir a Matrícula.</p>
								</h:commandLink> 
							</li>
						</ul>
					</div>				
				</td>
			</tr>
		</table>
		<br/>	
		<center><h:commandButton action="#{aproveitamento.continuarAproveitamento}" rendered="#{aproveitamento.permiteCancelarMatricula}" value="Aproveitar Outro Componente" /></center>
		<br/>	
	</c:if>				
	<table class="formulario" width="100%">

		<tr>
			<td>									
				<table class="subFormulario" width="100%">	
					<caption class="listagem">Dados do Aproveitamento ${(aproveitamento.cancelar)?" a ser Cancelado":""}</caption>
					<tr>
						<th class="obrigatorio">Componente Curricular: </th>
						<td colspan="3">
							<h:inputHidden id="idDisciplina" value="#{aproveitamento.obj.componente.id}"></h:inputHidden>
							<h:inputText id="nomeDisciplina" value="#{aproveitamento.obj.componente.nome}" size="80" disabled="#{aproveitamento.permiteCancelarMatricula}" />
								<ajax:autocomplete
								source="form:nomeDisciplina" target="form:idDisciplina"
								baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
								indicator="indicatorDisciplina" minimumCharacters="3"
								parameters="componentesAtivos=false,todosOsProgramas=true"
								parser="new ResponseXmlToHtmlListParser()" />
								<span id="indicatorDisciplina" style="display:none; ">
								<img src="/sigaa/img/indicator.gif" /> </span>													
						</td>
					</tr>
					<tr>
					<c:if test="${!aproveitamento.dispensa}">
						<th class="obrigatorio">Tipo de Aproveitamento:</th>
						<td>
							<c:if test="${not acesso.stricto and not acesso.coordenadorCursoStricto and not acesso.secretariaPosGraduacao}">
								<h:outputText value="#{aproveitamento.obj.situacaoMatricula.descricao }" rendered="#{aproveitamento.cancelar}" />
								<h:selectOneMenu value="#{aproveitamento.obj.situacaoMatricula.id}" onchange="mudarTipos()" disabled="#{aproveitamento.permiteCancelarMatricula}"
										id="situacao" rendered="#{!aproveitamento.cancelar}">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{aproveitamento.situacoesAproveitadasCombo}" />
								</h:selectOneMenu> 
							</c:if>
							
							<c:if test="${acesso.stricto or acesso.coordenadorCursoStricto or acesso.secretariaPosGraduacao}">
								<h:outputText value="#{aproveitamento.obj.situacaoMatricula.descricao }" rendered="#{aproveitamento.cancelar}" />
								<h:selectOneMenu value="#{aproveitamento.obj.situacaoMatricula.id}" onchange="mudarTipos()" disabled="#{aproveitamento.permiteCancelarMatricula}"
										id="situacao" rendered="#{!aproveitamento.cancelar}">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{aproveitamento.situacoesAproveitadasCombo}" />
								</h:selectOneMenu> 
							</c:if>
						</td>
					</c:if>
					<c:if test="${aproveitamento.atribuirNotaFrequencia && !aproveitamento.obj.discente.stricto}">
						<th class="obrigatorio">Ano-Período:</th>
						<td>
							<h:inputText id="inputAno" value="#{aproveitamento.obj.ano}" size="4" maxlength="4" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/> -
							<h:inputText id="inputPeriodo" value="#{aproveitamento.obj.periodo}" size="1" maxlength="1" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/>
						</td>
					</c:if>
					</tr>
					<tr id="linhaMediaFreq">
						<c:if test="${aproveitamento.atribuirNotaFrequencia}">
							<c:if test="${aproveitamento.nota}">
								<th class="obrigatorio">Média Final:</th>
								<td>
									<h:inputText value="#{aproveitamento.obj.mediaFinal}"  size="4" maxlength="4" disabled="#{aproveitamento.permiteCancelarMatricula}"
									 id="media" readonly="#{aproveitamento.cancelar}"  onkeydown="return formataValor(this, event, 1)">
									<f:converter converterId="convertNota"/>
									</h:inputText>
								</td>
							</c:if>
							<c:if test="${aproveitamento.conceito}">
								<th>Conceito:</th>
								<td>
								<span class="obrigatorio">&nbsp;</span>
									<h:selectOneMenu value="#{aproveitamento.obj.conceito}" id="conceito" disabled="#{aproveitamento.permiteCancelarMatricula}">
										<f:selectItem itemLabel="--" itemValue=""/>
										<f:selectItems value="#{conceitoNota.orderedCombo}" />
									</h:selectOneMenu>	
								</td>
							</c:if>
							<c:if test="${aproveitamento.competencia}">
								<th>Conceito:</th>
								<td>APTO</td>
							</c:if>
							<th>Frequência:</th>
							<td>
								<span class="obrigatorio"></span>
								<h:inputText value="#{aproveitamento.obj.numeroFaltas}" size="3" maxlength="3" id="frequencia" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/>
							</td>
						</c:if>	
					</tr>
					<c:if test="${ aproveitamento.obj.discente.stricto }">
						<tr>
							<th>Mês/Ano Início:</th>
							<td>
								<h:inputText id="inputMesInicio" value="#{aproveitamento.obj.mes}" size="2" maxlength="2" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/> /
								<h:inputText id="inputAnoInicio" value="#{aproveitamento.obj.anoInicio}" size="4" maxlength="4" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/>
							</td>
							<th>Mês/Ano Fim:</th>
							<td>
								<h:inputText id="inputMesFim" value="#{aproveitamento.obj.mesFim}" size="2" maxlength="2" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/> /
								<h:inputText id="inputAnoFim" value="#{aproveitamento.obj.anoFim}" size="4" maxlength="4" readonly="#{aproveitamento.cancelar}" onkeyup="formatarInteiro(this)" disabled="#{aproveitamento.permiteCancelarMatricula}"/>
							</td>
						</tr>
					</c:if>
					<tfoot>
					<tr>
						<td colspan="4" align="center">
							<h:commandButton value="Adicionar" action="#{aproveitamento.adicionarAproveitamento}" id="add" disabled="#{aproveitamento.permiteCancelarMatricula}" />
						</td>
					</tr>
					</tfoot>
		
				</table>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Confirmar"	action="#{aproveitamento.confirmar}" id="btConfirmar" disabled="#{aproveitamento.permiteCancelarMatricula}" />
					<h:commandButton value="<< Discentes" action="#{aproveitamento.buscarDiscente}" id="btDiscentes" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"	action="#{aproveitamento.cancelar}" id="btCancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<c:if test="${not empty aproveitamento.aproveitamentos}">
		<tr>
			<td colspan="4">
				<div class="infoAltRem">
			    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;" id="legenda"/>: Excluir Aproveitamento da Lista
				</div>
			</td>
		</tr>
			
		<t:dataTable id="tableAproveitamentos" value="#{aproveitamento.aproveitamentos}" var="aprov" align="center" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
			<f:facet name="caption">
				<h:outputText value="Aproveitamentos Adicionados" />
			</f:facet>
	
	
			<t:column rendered="#{aproveitamento.exibirTipoComponente}">
				<f:facet name="header">
					<f:verbatim>Tipo do Componente</f:verbatim>
				</f:facet>
				<h:outputText value="#{aprov.componente.tipoComponente.descricao}" />
			</t:column>
			
			<t:column rendered="#{aproveitamento.exibirTipoComponente}">
				<f:facet name="header">
					<f:verbatim>Tipo de Integralização</f:verbatim>
				</f:facet>
				<h:outputText value="#{aprov.tipoIntegralizacaoDescricao}" />
			</t:column>
			
			<c:if test="${aproveitamento.atribuirNotaFrequencia && !aproveitamento.obj.discente.stricto}">
				<t:column>
					<f:facet name="header">
						<f:verbatim><div align="center">Ano-Período</div></f:verbatim>
					</f:facet>
					<div align="center"><h:outputText value="#{aprov.ano}.#{aprov.periodo}" /></div>
				</t:column>
			</c:if>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Componente</f:verbatim>
				</f:facet>
				<h:outputText value="#{aprov.componenteDescricao}" />
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Tipo de Aproveitamento</f:verbatim>
				</f:facet>
				<h:outputText value="#{aprov.situacaoMatricula.descricao}" />
			</t:column>
			
			<c:if test="${aproveitamento.atribuirNotaFrequencia}">
				<t:column>
						<c:if test="${aproveitamento.nota && !aprov.dispensa}">
							<f:facet name="header">
								<f:verbatim><div align="right">Média Final</div></f:verbatim>
							</f:facet>
							<div align="right"><h:outputText value="#{aprov.mediaFinal}" /></div>
						</c:if>
						<c:if test="${aproveitamento.conceito && !aprov.dispensa}">
							<f:facet name="header">
								<f:verbatim><div align="center">Conceito</div></f:verbatim>
							</f:facet>
							<div align="center"><h:outputText value="#{aprov.conceitoChar}" /></div>
						</c:if>
						<c:if test="${aproveitamento.competencia && !aprov.dispensa}">
							<f:facet name="header">
								<f:verbatim><div align="right">Competência</div></f:verbatim>
							</f:facet>
							<div align="right"><h:outputText value="APTO" /></div>
						</c:if>
						<c:if test="${aprov.dispensa}">
							<div align="right"><h:outputText value="#{(aprov.dispensa)?'':aprov.frequencia}" /></div>
						</c:if>
				</t:column>
				
				<t:column>
					<f:facet name="header">
						<f:verbatim><div align="right">Frequência</div></f:verbatim>
					</f:facet>
					<div align="right"><h:outputText value="#{aprov.frequencia}" /></div>
				</t:column>
				
			</c:if>
			
			<t:column width="15" styleClass="centerAlign">
				<h:commandLink id="removerAproveitamento" title="Excluir Aproveitamento da Lista" onclick="#{confirmDelete}" action="#{aproveitamento.removeAproveitamento}" >
					<h:graphicImage url="/img/delete.gif"/>
					<f:param name="idComponente" value="#{aprov.componente.id}"/>
				</h:commandLink>
			</t:column>
			
		</t:dataTable>
	</c:if>
	
</h:form>	

	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<script type="text/javascript">
<!--
function mudarTipos() {
	var sel = $('form:situacao');
	var val = sel.options[sel.selectedIndex];
	if (val.value == '<%=SituacaoMatricula.APROVEITADO_DISPENSADO.getId()%>') {
		$('linhaMediaFreq').style.visibility = 'hidden';
	} else {
		$('linhaMediaFreq').style.visibility = 'visible';
	}
}
mudarTipos();
//-->
</script>
<script type="text/javascript">$('form:nomeDisciplina').focus();</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

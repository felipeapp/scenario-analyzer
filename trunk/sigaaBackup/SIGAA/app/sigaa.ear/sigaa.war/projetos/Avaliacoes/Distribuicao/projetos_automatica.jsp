<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>

	.numero_avaliadores {
		padding: 2px;
		font-size: 1.3em;
		text-align: center;
		color: #292;
	}

</style>

<script type="text/javascript">

var checkflag = "false";

function selectAllCheckBox() {
    var div = document.getElementById('form:listaProjetos');
    var e = div.getElementsByTagName("input");
   
    var i;

    if (checkflag == "false") {
            for ( i = 0; i < e.length ; i++) {
                    if (e[i].type == "checkbox"){ e[i].checked = true; }
            }
            checkflag = "true";
    } else {
            for ( i = 0; i < e.length ; i++) {
                    if (e[i].type == "checkbox"){ e[i].checked = false; }
            }
            checkflag = "false";
    }
}

</script>

<f:view>
	<h2><ufrn:subSistema /> > Distribuir Propostas</h2>

	<h:form id="form">
		<table class="formulario">
			<caption>Dados da Distribuição</caption>
				<tr>
					<th class="rotulo">Método de Distribuição:</th>
					<td>AUTOMÁTICA</td>
				</tr>
				<tr>	
					<th class="rotulo">Tipo de Avaliador:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Questionário:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.questionario.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Edital:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.edital.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Tipo de Avaliação:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.tipoAvaliacao.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Nota Mínima para Aprovação:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Discrepância Máxima Permitida:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.maximaDiscrepanciaAvaliacoes}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Total de Avaliações Efetuadas:</th>
					<td>
						<h:inputText id="totalAvaliacoesRealizadas" 
							value="#{distribuicaoProjetoMbean.totalAvaliacoesRealizadas}" 
							size="4" onkeyup="formatarInteiro(this);"/>
						<ufrn:help>Lista somente projetos com o total de avaliações indicado.</ufrn:help>
					</td>
				</tr>
			
				<tfoot>
					<tr>	
						<td colspan="2">
							<h:commandButton id="btAtualizar" action="#{distribuicaoProjetoMbean.atualizar}" value="Buscar"/>
							<h:commandButton id="btVoltar" value="<< Voltar" action="#{distribuicaoProjetoMbean.listar}" immediate="true"/>
						 	<h:commandButton id="btCancelar" value="Cancelar" onclick="#{confirm}" action="#{distribuicaoProjetoMbean.cancelar}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
				
		</table>
		
		<br/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Projeto
	    </div>
		<br/>
		
		<table class="listagem">
		    <caption>Lista de projetos para <h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/> (${fn:length(distribuicaoProjetoMbean.projetosDisponiveis)}) </caption>
	 		<tbody>
				<tr>
					<td>
						<h:panelGroup id="listaProjetos">
							<h:dataTable id="dtProjetos" value="#{distribuicaoProjetoMbean.projetosDisponiveis}" var="projeto" binding="#{distribuicaoProjetoMbean.projetosPossiveis}" 
								width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
								<t:column>
									<f:facet name="header"><h:outputText value="#" title="Avaliações Distribuídas."/></f:facet>
									<h:outputText value="#{projeto.totalAvaliacoesAtivas}" title="Avaliações Distribuídas."/>
								</t:column>
	
								<t:column>
									<f:facet name="header"><h:outputText value="@" title="Avaliações Efetuadas."/></f:facet>
	                                <h:outputText value="<font color='blue'>" escape="false"/>
									<h:outputText value="#{projeto.totalAvaliacoesAtivasRealizadas}" title="Avaliações Efetuadas."/>
									<h:outputText value="</font>" escape="false"/>
								</t:column>
	
								<t:column>
	                                <f:facet name="header"><h:outputText value="Max" title="Maior Nota das avaliações realizadas."/></f:facet>
	                                <h:outputText value="#{projeto.maxAvaliacao.nota}" title="Maior Nota das avaliações efetuadas.">
	                                	<f:convertNumber pattern="#0.0"/>
	                                </h:outputText>                  
	                            </t:column>
	
								<t:column>
	                                <f:facet name="header"><h:outputText value="Min" title="Menor Nota das avaliações realizadas."/></f:facet>
	                                <h:outputText value="#{projeto.minAvaliacao.nota}" title="Menor Nota das avaliações efetuadas.">
	                                     <f:convertNumber pattern="#0.0"/>
	                                </h:outputText>   
	                            </t:column>
	
								<t:column>
	                                <f:facet name="header"><h:outputText value="Dis" title="Discrepância entre a Maior e Menor Nota das avaliações realizadas."/></f:facet>
	                                <h:outputText value="<font color='red'>" escape="false" rendered="#{projeto.discrepanciaAvaliacao >= distribuicaoProjetoMbean.obj.modeloAvaliacao.maximaDiscrepanciaAvaliacoes}"/>
	                                <h:outputText value="#{projeto.discrepanciaAvaliacao}" title="Discrepância entre a Maior e Menor Nota das avaliações efetuadas.">
	                                     <f:convertNumber pattern="#0.0"/>
	                                </h:outputText>
	                                <h:outputText value="</font>" escape="false"/>
	                            </t:column>
	
								<t:column>
	                                <f:facet name="header"><h:outputText value="Med" title="Média das avaliações realizadas."/></f:facet>
	                                <h:outputText value="<font color='red'>" escape="false" rendered="#{projeto.media < distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/>
	                                <h:outputText value="<font color='blue'>" escape="false" rendered="#{projeto.media >= distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/>
	                                <h:outputText value="#{projeto.media}" title="Média das avaliações realizadas.">
	                                     <f:convertNumber pattern="#0.0"/>
	                                </h:outputText>   
	                                <h:outputText value="</font>" escape="false"/>
	                            </t:column>
	
								<t:column>
									<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
									<h:outputText value="#{projeto.ano}" />													
								</t:column>
	
								<t:column>
									<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
									<h:outputText value="#{projeto.titulo}" />													
								</t:column>
								
								
								<t:column>
									<f:facet name="header"><f:verbatim>Depto.</f:verbatim></f:facet>					
									<h:outputText value="#{projeto.unidade.sigla}" />					
								</t:column>
								
								
								<t:column width="2%">
									<h:commandLink  title="Visualizar" action="#{ projetoBase.view }" id="btView">
									      <f:param name="id" value="#{projeto.id}"/>
									      <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</t:column>
														
								<t:column>
									<f:facet name="header">
										<f:verbatim>
											<a href="javascript:selectAllCheckBox();"
												style="fontColor: blue">Todos</a>
										</f:verbatim>
									</f:facet>
									<h:selectBooleanCheckbox value="#{projeto.selecionado}" id="chkSelecionado"/>
								</t:column>
							</h:dataTable>
						
							<c:if test="${empty distribuicaoProjetoMbean.projetosDisponiveis}">
								<center><i> Nenhum projeto foi localizado.</i></center>
							</c:if>
						</h:panelGroup>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td style="text-align: center;">
						<b>Número de Avaliadores por Projeto:</b> &nbsp;
						<h:inputText id="numeroAvaliadoresProjeto" styleClass="numero_avaliadores"
							value="#{distribuicaoProjetoMbean.numeroAvaliadoresProjeto}" 
							size="2" maxlength="2" onkeyup="formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;">
						<h:commandButton id="btDistribuir" action="#{distribuicaoProjetoMbean.preDistribuir}" value="Distribuir"/>
						<h:commandButton id="btVoltar2" value="<< Voltar" action="#{distribuicaoProjetoMbean.listar}" immediate="true"/>
						<h:commandButton id="btCancelar2" value="Cancelar" onclick="#{confirm}" action="#{distribuicaoProjetoMbean.cancelar}" immediate="true"/>
			    	</td>
			    </tr>
			</tfoot>
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
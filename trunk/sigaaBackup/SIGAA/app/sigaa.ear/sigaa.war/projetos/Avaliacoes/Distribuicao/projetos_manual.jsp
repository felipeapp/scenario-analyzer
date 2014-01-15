<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuir Propostas</h2>

	<h:form id="form">
		<table class="formulario">
			<caption>Dados da Distribui��o</caption>
				<tr>
					<th class="rotulo">M�todo de Distribui��o:</th>
					<td>MANUAL</td>
				</tr>
				<tr>	
					<th class="rotulo">Tipo de Avaliador:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Question�rio:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.questionario.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Edital:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.edital.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Tipo de Avalia��o:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.tipoAvaliacao.descricao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Nota M�nima para Aprova��o:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Discrep�ncia M�xima Permitida:</th>
					<td><h:outputText value="#{distribuicaoProjetoMbean.obj.modeloAvaliacao.maximaDiscrepanciaAvaliacoes}"/></td>
				</tr>
				<tr>	
					<th class="rotulo">Total de Avalia��es Efetuadas:</th>
					<td>
						<h:inputText value="#{distribuicaoProjetoMbean.totalAvaliacoesRealizadas}" onkeyup="formatarInteiro(this);" maxlength="2" size="4"/>
						<ufrn:help>Lista somente projetos com o total de avalia��es indicado.</ufrn:help>
					</td>
				</tr>
			
				<tfoot>
					<tr>	
						<td colspan="2">
							<h:commandButton action="#{distribuicaoProjetoMbean.atualizar}" value="Buscar"/>
							<h:commandButton value="<< Voltar" action="#{distribuicaoProjetoMbean.listar}" immediate="true"/>
						 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{distribuicaoProjetoMbean.cancelar}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
				
		</table>
		
		<br/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Distribuir
		    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar
	    </div>
		<br/>
		
		<table class="listagem">
		    <caption>Lista de projetos para <h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/> (${fn:length(distribuicaoProjetoMbean.projetosDisponiveis)}) </caption>
	 		<tbody>
				<tr>
					<td>
				
						<h:dataTable id="dtProjetos" value="#{distribuicaoProjetoMbean.projetosDisponiveis}" var="projeto" binding="#{distribuicaoProjetoMbean.projetosPossiveis}" 
							width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

							<t:column>
								<f:facet name="header"><h:outputText value="#" title="Avalia��es Distribu�das."/></f:facet>
								<h:outputText value="#{projeto.totalAvaliacoesAtivas}" title="Avalia��es Distribu�das."/>
							</t:column>

							<t:column>
								<f:facet name="header"><h:outputText value="@" title="Avalia��es Efetuadas."/></f:facet>
                                <h:outputText value="<font color='blue'>" escape="false"/>
								<h:outputText value="#{projeto.totalAvaliacoesAtivasRealizadas}" title="Avalia��es Efetuadas."/>
								<h:outputText value="</font>" escape="false"/>
							</t:column>

							<t:column>
                                <f:facet name="header"><h:outputText value="Max" title="Maior Nota das avalia��es realizadas."/></f:facet>
                                <h:outputText value="#{projeto.maxAvaliacao.nota}" title="Maior Nota das avalia��es efetuadas.">
                                	<f:convertNumber pattern="#0.0"/>
                                </h:outputText>                  
                            </t:column>

							<t:column>
                                <f:facet name="header"><h:outputText value="Min" title="Menor Nota das avalia��es realizadas."/></f:facet>
                                <h:outputText value="#{projeto.minAvaliacao.nota}" title="Menor Nota das avalia��es efetuadas.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                            </t:column>

							<t:column>
                                <f:facet name="header"><h:outputText value="Dis" title="Discrep�ncia entre a Maior e Menor Nota das avalia��es realizadas."/></f:facet>
                                <h:outputText value="<font color='red'>" escape="false" rendered="#{projeto.discrepanciaAvaliacao >= distribuicaoProjetoMbean.obj.modeloAvaliacao.maximaDiscrepanciaAvaliacoes}"/>
                                <h:outputText value="#{projeto.discrepanciaAvaliacao}" title="Discrep�ncia entre a Maior e Menor Nota das avalia��es efetuadas.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>

							<t:column>
                                <f:facet name="header"><h:outputText value="Med" title="M�dia das avalia��es realizadas."/></f:facet>
                                <h:outputText value="<font color='red'>" escape="false" rendered="#{projeto.media < distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/>
                                <h:outputText value="<font color='blue'>" escape="false" rendered="#{projeto.media >= distribuicaoProjetoMbean.obj.modeloAvaliacao.mediaMinimaAprovacao}"/>
                                <h:outputText value="#{projeto.media}" title="M�dia das avalia��es realizadas.">
                                     <f:convertNumber pattern="#0.0"/>
                                </h:outputText>   
                                <h:outputText value="</font>" escape="false"/>
                            </t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
								<h:outputText value="#{projeto.ano}" />													
							</t:column>

							<t:column>
								<f:facet name="header"><f:verbatim>T�tulo</f:verbatim></f:facet>
								<h:outputText value="#{projeto.titulo}" />													
							</t:column>
							
							
							<t:column>
								<f:facet name="header"><f:verbatim>Depto.</f:verbatim></f:facet>					
								<h:outputText value="#{projeto.unidade.sigla}" />					
							</t:column>
							
							<t:column width="2%">
								<h:commandLink title="Distribuir"
									action="#{distribuicaoProjetoMbean.preDistribuir}"
									rendered="#{projeto.permitidoAvaliar}" id="btPreDistribuir" immediate="true">
									<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
							</t:column>
							
							<t:column width="2%">
								<h:commandLink  title="Visualizar" action="#{ projetoBase.view }" id="btView">
								      <f:param name="id" value="#{projeto.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>						
						</h:dataTable>
					
						<c:if test="${empty distribuicaoProjetoMbean.projetosDisponiveis}">
							<center><i> Nenhum projeto foi localizado.</i></center>
						</c:if>
				</td>
			</tr>
				
			</tbody>
		 </table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
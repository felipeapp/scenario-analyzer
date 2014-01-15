<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script>
  <!--

	function transferir(idOrigem, idDestino) {
		$(idOrigem).value = $F(idDestino);
	}
  
	function justificativa(obj, mat){
			just = $('justificativa' + mat);				

			if(obj.value == 'false'){
			   just.show();
			}else{
				just.hide();										
			}
	}
  
  
  
//-->
</script>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2><ufrn:subSistema /> > Cadastro de Resultados da Seleção de Monitores</h2>
	<h:form id="frmSelecao">

		<h:outputText value="#{discenteMonitoria.create}"/>
		<input type="hidden" value="${discenteMonitoria.provaSelecao.id}" name="idProva" id="idProva"/>		

		<div class="descricaoOperacao">
			<b>Atenção: </b><br/>
			De acordo com a RESOLUÇÃO No 169/2008-CONSEPE, de 02 de dezembro de 2008, a distribuição de			
			bolsas da ${ configSistema['siglaInstituicao'] } será prioritária para alunos que se enquadrem na condição sócio-econômica carente.<br/>
		    <br/>
			<b>Os critérios de desempate são na seguinte ordem:</b><br/>
			a) maior nota na prova seletiva;<br/>
			b) maior nota no(s) componente(s) curricular(es) de formação objeto da seleção;<br/>
			c) maior Índice de Rendimento Acadêmico (IRA).<br/>
		</div>								
	
		<br/>

		<div class="infoAltRem">
		     	<h:graphicImage value="/img/check.png" style="overflow: visible;"/>:  Discente prioritário
				<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;"/>: Discente NÃO prioritário
		</div>

		<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Lista de Monitores inscritos </caption>

					<tr>
						<td >
							<t:dataTable value="#{discenteMonitoria.discentesInscritosNaoCadastrados}" 
								var="inscrito" rowClasses="linhaPar,linhaImpar" width="100%" id="inscrito" rowIndexVar="linha">

								<t:column width="60%">
									<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
									<h:outputText value="#{inscrito.discente.matriculaNome}" id="matricula_nome_discente_"/>
								</t:column>
			
								<t:column width="3%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>P. Escrita</f:verbatim></f:facet>
									<h:inputText value="#{inscrito.notaProva}" 
									size="4" maxlength="4" onkeypress="return formataValor(this, event, 1)" onblur="verificaNotaMaiorDez(this)" id="nota_prova_escrita_">
												<f:converter converterId="convertNota"/>
									</h:inputText>									
								</t:column>
			
								<t:column width="3%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>P. Final</f:verbatim></f:facet>
									<h:inputText value="#{inscrito.nota}" size="4" maxlength="4" 
										onkeypress="return formataValor(this, event, 1)" onblur="verificaNotaMaiorDez(this)" id="nota_prova_final_">
										<f:converter converterId="convertNota"/>
									</h:inputText>									
								</t:column>
																
								<t:column>
									<f:facet name="header"><f:verbatim>Situação</f:verbatim>
									</f:facet>
									<h:selectOneMenu value="#{inscrito.classificado}" style="width: 95px" 
										onchange="javascript:justificativa(this, #{inscrito.discente.matricula})" id="situacao_desistente_">
										<f:selectItem itemValue="true" itemLabel="Classificado"/>
										<f:selectItem itemValue="false" itemLabel="Não Classificado"/>										
									</h:selectOneMenu>
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Prioritário</f:verbatim></f:facet>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{inscrito.prioritario}" title="Discente prioritário segundo a RESOLUÇÃO No 169/2008-CONSEPE."/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!inscrito.prioritario}" title="Discente não prioritário segundo a RESOLUÇÃO No 169/2008-CONSEPE."/>
								</t:column>

								<t:column styleClass="centerAlign">
									<h:outputText value="<div id='justificativa#{inscrito.discente.matricula}' #{inscrito.classificado ? 'style=display:none':''}>"  escape="false" />
										<f:facet name="header"><f:verbatim>Observação <div class="obrigatorio" /></f:verbatim></f:facet>
										<h:inputText value="#{inscrito.observacao}" size="20" id="justificativa_nao_classificado_"/>
									<f:verbatim></div></f:verbatim>
								</t:column>								
							</t:dataTable>
							
							<c:if test="${empty discenteMonitoria.discentesInscritosNaoCadastrados}">
						        <center>
									<font color="red">Não há discentes disponíveis</font>
								</center>
							</c:if>
							
							
					</td>
				</tr>
		</tbody>
		<tfoot>
				<tr>
					<td>
						<h:commandButton id="btAdicionarResultadoSelecao" value="Incluir Discentes na Seleção" action="#{discenteMonitoria.adicionarResultadoSelecao}" rendered="#{not empty discenteMonitoria.discentesInscritosNaoCadastrados}"/>
					</td>
				</tr>
		</tfoot>
	</table>
		

<br/>
<br/>


	<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Cadastro de Resultados da Seleção de Monitores </caption>


				<tr>
					<td colspan="2">
		
							<table width="100%" cellpadding="3">
		
								<tr>
									<th width="23%" class="rotulo">Projeto de Ensino:</th>
									<td> <h:outputText  id="nomeCompCurricular" value="#{discenteMonitoria.provaSelecao.projetoEnsino.titulo}"/>  </td>
								</tr>
								
					            <tr>
					                <th class="rotulo">Prova: </th> 
					                <td>${discenteMonitoria.provaSelecao.titulo}</td>
					            </tr>
					            
					            <tr>
					                <th class="rotulo">Data da Prova: </th> 
					                <td><fmt:formatDate value="${discenteMonitoria.provaSelecao.dataProva}" pattern="dd/MM/yyyy" /></td>
					            </tr>
					            
								<tr>
									<th class="rotulo" >Bolsas Remuneradas :</th><td> <h:outputText value="#{discenteMonitoria.provaSelecao.vagasRemuneradas}"/></td>
								</tr>
								
								<tr>							
									<th class="rotulo">Bolsas Não Remuneradas:</th><td> <h:outputText value="#{discenteMonitoria.provaSelecao.vagasNaoRemuneradas}"/> </td>
								</tr>
		
							</table>
	
					</td>
				</tr>
				
				


				<c:if test="${ not empty discenteMonitoria.provaSelecao.resultadoSelecao }">
					<tr>
						<td colspan="7">
							<div class="infoAltRem">
						    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Excluir Monitor da Lista
							</div>			
						</td>
					</tr>
					<tr>
						<td colspan="7"></td>
					</tr>
		
					<tr>
						<td colspan="2">
						
							<t:dataTable value="#{discenteMonitoria.provaSelecao.resultadoSelecao}" 
								var="selecao" rowClasses="linhaPar,linhaImpar" width="100%" id="selecao" rowIndexVar="linha">
			
								<t:column width="40%">
									<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
									<h:outputText value="#{selecao.discente.matricula}"/><f:verbatim> - </f:verbatim>
									<h:outputText value="#{selecao.discente.pessoa.nome}"/>
								</t:column>
			
								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>NPE</f:verbatim></f:facet>
									<h:outputText value="#{selecao.notaProva}" />
								</t:column>
			
								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>NF</f:verbatim></f:facet>
									<h:outputText value="#{selecao.nota}"/>
								</t:column>

								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>MCP</f:verbatim></f:facet>
									<h:outputText value="#{selecao.mediaComponentesProjeto}"/>
								</t:column>

								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>IA(IRA)</f:verbatim></f:facet>
									<h:outputText value="#{selecao.indiceAcademicoSelecao.valor}" rendered="#{not empty selecao.indiceAcademicoSelecao}"/>
									<h:outputText value="-" rendered="#{empty selecao.indiceAcademicoSelecao}"/>
								</t:column>
			
								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Class.</f:verbatim></f:facet>
									<h:outputText value="#{selecao.classificacaoView}"/>
								</t:column>
			
								<t:column width="10%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Vínculo</f:verbatim></f:facet>
									<h:outputText value="#{selecao.tipoVinculo.descricao}"/>
								</t:column>
			
								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"></f:facet>									
										<h:commandLink action="#{discenteMonitoria.removeSelecao}" style="border: 0;" 
											title="Excluir Monitor da Lista"
											onclick="return confirm('Atenção! Tem certeza que gostaria de remover esse resultado?');" 
											rendered="#{discenteMonitoria.provaSelecao.permitidoAlterar}" id="remover_resultado_discente_">
										      <f:param name="idDiscente" value="#{selecao.discente.id}"/>
										      <h:graphicImage url="/img/delete.gif" />
										</h:commandLink>											
								    	<h:graphicImage value="/img/monitoria/user1_preferences.png" style="overflow: visible;" rendered="#{! discenteMonitoria.provaSelecao.permitidoAlterar}" styleClass="noborder" />
								</t:column>
								
							</t:dataTable>
							<hr/>
							[<b>NPE</b> - Nota da Prova Escrita, <b>NF</b> - Nota Final, <b>MCP</b> - Média dos Componentes da Prova,  <b>IA</b> - Índice Acadêmico, <b>Class.</b> - Classificação]
						</td>
					</tr>
				
				</c:if>
		</tbody>
		<tfoot>
				<tr>
					<td colspan="2">
	
						<h:commandButton id="bt_cancelar_" value="Cancelar" action="#{discenteMonitoria.cancelar}" onclick="#{confirm}"/>
						<h:commandButton id="bt_avancar_" value="Avançar >>" action="#{discenteMonitoria.selecionaOrientadores}"/>
	
					</td>
				</tr>
		</tfoot>

	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
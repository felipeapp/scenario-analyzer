<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script type="text/javascript">
	J = jQuery.noConflict();
</script>
<f:view>
	<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />
	<h2> <ufrn:subSistema /> &gt; ${planoDocenciaAssistidaMBean.tituloFormBusca}</h2>
<h:form id="form" prependId="false">

	<table class="formulario" style="width: 80%">
	<caption> Informe os critérios de Busca</caption>
		<tr>
			<c:if test="${acesso.ppg || acesso.membroApoioDocenciaAssistida}">
				<td width="5px">
					<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroUnidade}" id="checkUnidade" styleClass="noborder" />
				</td>		
				<td width="155px">
					<label for="checkUnidade">Programa:	</label>				
				</td>				
				<td>
					<h:selectOneMenu id="programa" style="width : 550px;" onchange="J('#checkUnidade').attr('checked', 'true');" value="#{planoDocenciaAssistidaMBean.unidade.id}">
						<f:selectItem itemLabel="Todos" itemValue="0"/>
						<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
					</h:selectOneMenu>
				</td>
			</c:if>			
			<c:if test="${!acesso.ppg && !acesso.membroApoioDocenciaAssistida}">
				<td width="5px"></td>
				<th width="155px" style="font-weight: bold;">Programa:</th>
				<td>${planoDocenciaAssistidaMBean.programaStricto}</td>				
			</c:if>		
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroStatus}" id="checkStatus" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkStatus">Situação:</label>
			</td>				
			<td>
				<h:selectOneMenu id="situacao" style="width : 250px;" onchange="J('#checkStatus').attr('checked', 'true');" value="#{planoDocenciaAssistidaMBean.status}">
					<f:selectItem itemLabel=" Todos " itemValue="0"/>
					<f:selectItems value="#{planoDocenciaAssistidaMBean.allStatus}"/>
				</h:selectOneMenu>
			</td>
		</tr>				
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroNivel}" id="checkNivel" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkNivel">Nível:</label>				
			</td>				
			<td>
			   	<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.nivel}" style="width : 250px;" onchange="J('#checkNivel').attr('checked', 'true');" id="nivelCombo">
					<f:selectItem itemLabel="Todos" itemValue="0"/>
					<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
				</h:selectOneMenu>				
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroModalidade}" id="checkTipoBolsa" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkTipoBolsa">Modalidade da Bolsa:</label>				
			</td>				
			<td>
			   	<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.modalidadeBolsa.id}" style="width : 250px;" onchange="J('#checkTipoBolsa').attr('checked', 'true');" id="modalidadeCombo">
					<f:selectItem itemLabel="Todas" itemValue="0"/>
					<f:selectItems value="#{planoDocenciaAssistidaMBean.allModalidadeBolsaCombo}"/>
				</h:selectOneMenu>				
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroDiscente}" id="checkDiscente" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkDiscente">Discente:</label>				
			</td>				
			<td>
			   	<h:inputText value="#{planoDocenciaAssistidaMBean.discente}" onchange="J('#checkDiscente').attr('checked', 'true');" id="txtDiscente" maxlength="60" style="width: 500px;"/>
			   	<ufrn:help>Informe o nome ou matrícula do discente.</ufrn:help>			
			</td>
		</tr>		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroComponente}" id="checkComponente" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkComponente">Componente Curricular:</label>				
			</td>				
			<td>
				<a4j:outputPanel>
					<h:inputText value="#{planoDocenciaAssistidaMBean.componente.nome}" id="nomeComponente" style="width: 500px;" onchange="J('#checkComponente').attr('checked', 'true');"/> 
					<rich:suggestionbox id="sbComponenteCurricular" width="400" height="120" for="nomeComponente" 
						minChars="6" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200"
						suggestionAction="#{componenteCurricular.autocompleteGraduacao}"  var="_componente" fetchValue="#{_componente.nome}">
						<h:column>
							<h:outputText value="#{_componente.codigo}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_componente.nome}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_componente.unidade.sigla}"/>
						</h:column>
						<a4j:support event="onselect" actionListener="#{planoDocenciaAssistidaMBean.selecionarComponente}">
							<f:attribute name="componente" value="#{_componente}"/>
						</a4j:support>
					</rich:suggestionbox>	
		            <a4j:status id="statusComponente">
		                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
		            <ufrn:help>Informe o código ou nome do Componente Curricular.</ufrn:help>
	           </a4j:outputPanel>			   				
			</td>
		</tr>		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroAtividade}" id="checkAtividade" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkAtividade">Atividade:</label>				
			</td>				
			<td>
				<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.formaAtuacao.id}" onchange="J('#checkAtividade').attr('checked', 'true');" style="width: 250px;" id="atividadeCombo">
					<f:selectItem itemValue="-1" itemLabel="Todos"/>
					<f:selectItem itemValue="0" itemLabel="OUTRA"/>
					<f:selectItems value="#{planoDocenciaAssistidaMBean.formasAtuacaoCombo}"/>
				</h:selectOneMenu>		
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroCargaHoraria}" id="checkCargaHoraria" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkCargaHoraria">Carga Horária:</label>				
			</td>				
			<td>
				<h:inputText value="#{planoDocenciaAssistidaMBean.chInicial}" onchange="J('#checkCargaHoraria').attr('checked', 'true');" id="txCHInicial"
				onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" style="width: 40px;" maxlength="3"/> 
				<h:outputText value=" a "/> 
				<h:inputText value="#{planoDocenciaAssistidaMBean.chFinal}" onchange="J('#checkCargaHoraria').attr('checked', 'true');" id="txCHFinal"
				onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" style="width: 40px;" maxlength="3"/>
			</td>
		</tr>				
		<a4j:outputPanel rendered="#{!planoDocenciaAssistidaMBean.buscaSemIndicacao}">
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroTipoBolsa}" id="checkTipoPlano" styleClass="noborder" />
				</td>		
				<td>
					<label for="checkTipoPlano">Tipo do Plano:</label>				
				</td>				
				<td>
				   	<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.tipoBolsa}" style="width: 250px;" onchange="J('#checkTipoPlano').attr('checked', 'true');" id="tipoPlanoCombo">
						<f:selectItem itemLabel="Todos" itemValue="0"/>
						<f:selectItem itemLabel="COM INDICAÇÃO" itemValue="1"/>
						<f:selectItem itemLabel="SEM INDICAÇÃO" itemValue="2"/>
					</h:selectOneMenu>				
				</td>
			</tr>	
		</a4j:outputPanel>					
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{planoDocenciaAssistidaMBean.filtroAnoPeriodo}" id="checkAnoPeriodo" styleClass="noborder" />
			</td>
			<td>
				<label for="checkAnoPeriodo">Ano-Período:</label>
			</td>
			<td>
				<h:inputText value="#{planoDocenciaAssistidaMBean.ano}" onchange="J('#checkAnoPeriodo').attr('checked', 'true');" size="4" maxlength="4" 
						id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> .
				<h:inputText value="#{planoDocenciaAssistidaMBean.periodo}" onchange="J('#checkAnoPeriodo').attr('checked', 'true');" size="1" maxlength="1" 
						id="inputPeriodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
			</td>
		</tr>
	
		<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton id="btBuscar" action="#{planoDocenciaAssistidaMBean.buscar}" value="Buscar"/>
				<h:commandButton id="btCancelar" action="#{planoDocenciaAssistidaMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
		</tfoot>
	</table>	
	<br/>
	<c:set var="planos" value="#{planoDocenciaAssistidaMBean.planosSemIndicacao}"/>
	
	<c:if test="${empty planos}">
		<table class="listagem" style="width: 100%">
			<caption class="listagem">Planos de Docência Assistida Cadastrados</caption>
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhum Plano de Docência Assistida encontrado.</i>
					</td>
				</tr>
		</table>
	</c:if>
	<c:if test="${not empty planos}">
		<a4j:outputPanel rendered="#{!planoDocenciaAssistidaMBean.buscaSemIndicacao}">		
			<div style="text-align:center;margin-left: auto;">
				<h:commandLink action="#{ planoDocenciaAssistidaMBean.imprimirBusca }"  title="Versão para Impressão" id="btimpressaoBusca">
					<h:graphicImage value="../../../shared/img/printer.png"/>
					<p style="font-variant:small-caps;font-size:1.3em;font-weight:bold;">Versão para Impressão</p> 
				</h:commandLink>
			</div>		
			<br/>
		</a4j:outputPanel>							
		<center>
			<div class="infoAltRem">
				<h:form>			
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Plano
					<h:graphicImage value="/img/table_go.png" style="overflow: visible;"/>: Analisar Relatório Semestral					
					<c:if test="${acesso.ppg}">
						<h:graphicImage value="/img/alterar.gif"/>: Alterar Plano de Docência Assistida
					</c:if>	
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Docência Assistida<br/>					
					<h:graphicImage value="/img/report.png"/>: Visualizar Relatório Semestral						
					<h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;"/>: Download do Arquivo Anexado
					<h:graphicImage value="/img/cal_prefs.png" style="overflow: visible;"/>: Visualizar Histórico de Movimentações<br/>
					<a4j:outputPanel rendered="#{!planoDocenciaAssistidaMBean.buscaSemIndicacao}">
				    	<h:graphicImage value="/img/star.png" style="overflow: visible;"/>: Possui Indicação de bolsa REUNI.
				    </a4j:outputPanel>					
				</h:form>
			</div>
		</center>
		
		<%@include file="include_lista_plano_docencia.jsp"%>	
	</c:if>
</h:form>		
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
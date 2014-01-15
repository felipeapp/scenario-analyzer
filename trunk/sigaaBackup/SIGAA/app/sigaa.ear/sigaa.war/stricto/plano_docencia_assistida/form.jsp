<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>

<style>
	.dados th {
		font-weight: bold;
	}
</style>


<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<h2> <ufrn:subSistema /> &gt; Plano de Docência Assistida</h2>

<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />

<c:if test="${!planoDocenciaAssistidaMBean.portalPpg}">
<div class="descricaoOperacao">
	<p>
		<b> Caro Aluno, </b>
	</p> 	
	<p>
		Através deste formulário será possível realizar o cadastro do Plano de Docência Assistida, 
		o qual será enviado para o coordenador do seu programa para análise.
	</p>
</div>
</c:if>

<c:if test="${planoDocenciaAssistidaMBean.obj.reprovado or planoDocenciaAssistidaMBean.obj.solicitadoAlteracao}">
	<div style="margin : 5px auto 20px auto; margin-bottom:5px; margin-left : auto; width: 90%">
		<span style="color:red;">
			<h3 style="text-align: center; margin-bottom: 10px;"><b>ATENÇÃO</b></h3>
			<p style="text-align: center;">Seu Plano de Docência Assistida foi analisado e encontra-se com o Status: <b>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</b>.</p>
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.observacao}">
				<br/> Motivo:
				<p style="text-align: justify; text-indent: 20px;">${planoDocenciaAssistidaMBean.obj.observacao}</p>
			</c:if>
			<br/>
		</span>				
	</div>
</c:if>


<h:form id="form">
<table class="formulario" style="width: 90%">
	<caption> Cadastro de Plano de Docência Assistida</caption>
	<tr>
		<td colspan="2" class="subFormulario"> Dados do Aluno de Pós-Graduação</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="dados" style="width: 100%;">
				<tr>
					<th style="width: 35%;">
						Nome:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.obj.discente.matricula} - ${planoDocenciaAssistidaMBean.obj.discente.pessoa.nome}
					</td>		
				</tr>
				<tr>
					<th>
						Programa:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.obj.discente.unidade.nome}
					</td>		
				</tr>	
				<tr>
					<th>
						Orientador:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}
					</td>		
				</tr>		
				<tr>
					<th>
						Nível:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.obj.discente.nivelDesc}
					</td>		
				</tr>	
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
					<tr>
						<th>
							Período da Indicação:
						</th>
						<td>
						    ${planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa.anoPeriodoFormatado}
						</td>		
					</tr>			
				</c:if>	
				<c:if test="${empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
					<tr>
						<th>
							Ano/Período de Referência:
						</th>
						<td>
						    ${planoDocenciaAssistidaMBean.obj.ano}.${planoDocenciaAssistidaMBean.obj.periodo}
						</td>		
					</tr>			
				</c:if>									
			</table>
		</td>
	</tr>
	
	<c:if test="${not planoDocenciaAssistidaMBean.obj.reuni}">
		<tr>
			<td colspan="2" class="subFormulario"> Dados de Bolsa</td>
		</tr>	
		<tr>
			<th class="obrigatorio">
				Bolsista:
			</th>
			<td>
			    <h:selectOneRadio id="possuiBolsa" onclick="escolher(this)" value="#{planoDocenciaAssistidaMBean.bolsista}">
			    	<f:selectItems value="#{planoDocenciaAssistidaMBean.simNao}"/>
			    </h:selectOneRadio>
			</td>				
		</tr>
		<tr id="modalidade">
			<th class="obrigatorio">
				Modalidade da Bolsa:
			</th>	
			<td>		 
			   	<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.obj.modalidadeBolsa.id}" onchange="submit()" style="width: 70%" id="modalidadeCombo">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
					<f:selectItem itemValue="0" itemLabel="OUTRA"/>
					<f:selectItems value="#{planoDocenciaAssistidaMBean.allModalidadeBolsaCombo}"/>
					<a4j:support event="onchange" reRender="form:nomeBolsa"/>
				</h:selectOneMenu>						    
			</td>
		</tr>
		<c:if test="${planoDocenciaAssistidaMBean.obj.modalidadeBolsa.id == 0 && planoDocenciaAssistidaMBean.bolsista}">
			<tr>
				<th class="obrigatorio">
					Outra Bolsa:
				</th>	
				<td>
					<h:inputText id="outraModalidade" size="60" onkeyup="CAPS(this);" onblur="submit()" maxlength="100" value="#{planoDocenciaAssistidaMBean.obj.outraModalidade}"/>
				</td>
			</tr>
		</c:if>
	</c:if>		
	
	<tr>
		<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
	</tr>	
	<tr>
		<th class="obrigatorio" style="text-align: right;">Curso: </th>
		<td>
			<a4j:region>
				<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.obj.curso.id}" style="width: 70%" id="cursoCombo">
					<f:selectItem itemValue="0" itemLabel="-- Selecione um curso de graduação --"/>
					<f:selectItems value="#{curso.allCursosGraduacaoPresenciaisCombo}"/>					
					<a4j:support event="onblur" reRender="turmaCombo" actionListener="#{planoDocenciaAssistidaMBean.selecionarCurso}"/>
				</h:selectOneMenu>
				<rich:spacer width="10"/>
	            <a4j:status>
	                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
	            </a4j:status>				
			</a4j:region>						
		</td>
	</tr>	
	<tr>
		<th class="obrigatorio">Componente Curricular:</th>
		<td>
			<a4j:outputPanel>
				<h:inputText value="#{planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}" id="nomeComponente" style="width: 500px;"/> 
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
					<a4j:support event="onselect" reRender="form,turmaCombo" actionListener="#{planoDocenciaAssistidaMBean.selecionarComponente}">
						<f:attribute name="componente" value="#{_componente}"/>
					</a4j:support>
				</rich:suggestionbox>	
	            <a4j:status id="statusComponente">
	                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
	            </a4j:status>
           </a4j:outputPanel>
		</td>
	</tr>
	
	<a4j:region rendered="#{!empty planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}">
	<tr>
		<th style="text-align: right; font-weight: bold;">Departamento: </th>
		<td>
			<h:outputText id="departamento" value="#{planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}"/>					
		</td>
	</tr>	
	</a4j:region>
	
	<a4j:region id="dadosDocente" rendered="#{planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
		<tr id="linhaDocente">
			<th class="obrigatorio">Docente:</th>
			<td>								
				<h:inputText value="#{planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
				<rich:suggestionbox width="400" height="100" for="nomeDocente" id="sbDocente"
					minChars="3" nothingLabel="#{servidor.textoSuggestionBox}"
					suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}"
					onsubmit="$('form:imgStDocente').style.display='inline';" 
				    oncomplete="$('form:imgStDocente').style.display='none';">
				    
					<h:column>
						<h:outputText value="#{_servidor.siape}"/>
					</h:column>
					
					<h:column>
						<h:outputText value="#{_servidor.nome}"/>
					</h:column>
	
	                   <a4j:support event="onselect">
						<f:setPropertyActionListener value="#{_servidor.id}" target="#{planoDocenciaAssistidaMBean.obj.servidor.id}" />
				    </a4j:support>			
				    				
				</rich:suggestionbox>
				<h:graphicImage id="imgStDocente" style="display:none; overflow: visible;" value="/img/indicator.gif"/>				
			</td>
		</tr>		
	</a4j:region>
	
	<a4j:region id="dadosTurma" rendered="#{!planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
	<tr>
		<td colspan="4">
			<table class="subFormulario" style="border: 1px solid #cacaca" width="100%">
				<caption>Turma(s)</caption>
				<tr>
					<th class="obrigatorio" style="width: 180px; text-align: right;">Turma:</th>
					<td colspan="3">
						<a4j:region>
							<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.turmaDocencia.turma.id}" style="width: 70%" id="turmaCombo">
								<f:selectItem itemValue="0" itemLabel="-- Selecione a Turma --"/>
								<f:selectItems value="#{planoDocenciaAssistidaMBean.comboTurmas}"/>					
								<a4j:support event="onblur" reRender="datasTurmaInicio,datasTurmaFim" actionListener="#{planoDocenciaAssistidaMBean.selecionarTurma}"/>
							</h:selectOneMenu>
							<rich:spacer width="10"/>
				            <a4j:status>
				                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
				            </a4j:status>				
						</a4j:region>			
					</td>
				</tr>
				<tr>
					<th class="obrigatorio" >Data de Início:</th>
					<td>
						<a4j:outputPanel id="datasTurmaInicio">	
							<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
										maxlength="10" onkeypress="return formataData(this,event)" value="#{planoDocenciaAssistidaMBean.turmaDocencia.dataInicio}" id="dataInicioTurma" />
						</a4j:outputPanel>	
					</td>
					<th class="obrigatorio" >Data de Fim:</th>
					<td>
						<a4j:outputPanel id="datasTurmaFim">
							<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
										maxlength="10" onkeypress="return formataData(this,event)" value="#{planoDocenciaAssistidaMBean.turmaDocencia.dataFim}" id="datafimTurma" />
						</a4j:outputPanel>
					</td>
				</tr>
				<tfoot>
					<tr>
						<td  colspan="4">
							<a4j:commandButton title="Adicionar Turma" id="addTurma" reRender="listaTurma" value="Adicionar Turma" actionListener="#{planoDocenciaAssistidaMBean.addTurma}"/>
							<rich:spacer width="10"/>
					        <a4j:status>
					             <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
					        </a4j:status>																		
						</td>
					</tr>
				</tfoot>
			</table>		
		</td>
	</tr>
	<tr>
		<td colspan="2">							
			<a4j:outputPanel id="listaTurma">			
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}">						
					<div class="infoAltRem">
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
						Remover Turma
					</div>
				</c:if>
							
				<t:dataTable value="#{planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}" var="item" style="width: 100%;" 
					styleClass="listagem" id="listagemTurmas" rowClasses="linhaPar, linhaImpar" rowIndexVar="row" rendered="#{not empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}">
					<t:column>
						<f:facet name="header"><f:verbatim>Turma(s)</f:verbatim></f:facet>						
						<h:outputText value="#{item.turma.codigo} - #{item.turma.disciplina.nome} (#{item.turma.anoPeriodo})"/>																		
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Docente(s)</f:verbatim></f:facet>						
						<h:outputText value="#{item.turma.docentesNomes}"/>																		
					</t:column>					
						<t:column headerstyle="text-align:center" style="text-align:center">
							<f:facet name="header"><f:verbatim>Data de Início</f:verbatim></f:facet>
							<h:outputText value="#{item.dataInicio}"/>
						</t:column>
						<t:column headerstyle="text-align:center" style="text-align:center">
							<f:facet name="header"><f:verbatim>Data de Fim</f:verbatim></f:facet>
							<h:outputText value="#{item.dataFim}"/>					
						</t:column>
					<t:column width="5%" styleClass="centerAlign">
						<a4j:commandLink actionListener="#{planoDocenciaAssistidaMBean.removerTurma}" reRender="listaTurma" title="Remover Turma" id="linkRemTurma">
								<h:graphicImage value="/img/delete.gif"/>
								<f:param name="indice" value="#{row}"/>
						</a4j:commandLink>						
					</t:column>					
				</t:dataTable>						
			</a4j:outputPanel>	
		</td>		
	</tr>			
	
	</a4j:region>		 					
	<tr>
		<td colspan="2" class="subFormulario">Justificativa para escolha do componente curricular:<span class="obrigatorio">&nbsp;</span></td>
	</tr>						 							 					
	<tr>
		<td colspan="2" align="center"> 
			<h:inputTextarea id="txjustificativa" cols="110" rows="3" value="#{ planoDocenciaAssistidaMBean.obj.justificativa }"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario">Objetivos:<span class="obrigatorio">&nbsp;</span></td>
	</tr>		
	<tr>
		<td colspan="2" align="center">
			<h:inputTextarea id="txObjetivos" cols="110" rows="3" value="#{ planoDocenciaAssistidaMBean.obj.objetivos }"/> 
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario"> Atividades:<span class="obrigatorio">&nbsp;</span></td>
	</tr>
	<tr>
		<td colspan="2">
			<%@include file="_atividade.jsp"%>			
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Cancelar" action="#{planoDocenciaAssistidaMBean.cancelarGeral}" onclick="#{confirm}" immediate="true" id="btCancelOp"/>
				<h:commandButton value="Avançar >>" action="#{planoDocenciaAssistidaMBean.confirmar}" id="btAvancar"/>
			</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<script type="text/javascript">
<c:if test="${!planoDocenciaAssistidaMBean.obj.reuni}">
	function escolher(obj) {	
		if (obj.checked == true && obj.value == 'true') {
			$('modalidade').show();
		} else {
			$('modalidade').hide();
			$('nomeBolsa').hide();
		}
	}
	
	J(document).ready(function(){
		escolher(document.getElementById("form:possuiBolsa:0"));
	});
</c:if>
</script>

<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>


<script type="text/javascript">

	var checkflag = "false";

	function selectAllCheckBox() {
	    var div = document.getElementById('form');
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

<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}

</script>

<f:view>
	<a4j:keepAlive beanName="relatorioFrequenciaAssitenteSocial"/>
	<h2><ufrn:subSistema /> > Relatório de Frequência</h2>
	
	<h:form id="form">
	
		<div class="descricaoOperacao">
			<p>
			<br />Caro Usuário, esta operação destina-se a gerar um relatório de frequência dos discentes.
			<br />Inicialmente selecione o polo, módulo e período desejados;
			<br />Em seguida selecione as turmas para que deseja gerar o relatório e será dada duas opções de gerar o relatório;
			<br /><br /><b>Gerar Relatório: </b> Gera um relatório com todos os discentes das turmas selecionadas.
			<br /><b>Gerar Relatório com Filtro:</b> Nesse relatório é possível filtrar a frequência dos discentes por período, quantidade de faltas e percentual de faltas.
			<br />
			</p>
			
		</div>
		
    
		<table class="formulario" width="90%">
			<caption class="listagem">Consultar turmas</caption>
			<tr>
				<td colspan="2">
					<h:inputHidden value="#{relatorioFrequenciaAssitenteSocial.idOpcaoPoloSelecionado}"/>
				</td>
			<tr>
			
			<tr>
				<th class="obrigatorio">Opção - Pólo - Grupo:</th>
				<td>
					<h:selectOneMenu value="#{relatorioFrequenciaAssitenteSocial.idOpcaoPoloSelecionado}" id="opcaoPoloGrupo" required="true" >
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{relatorioFrequenciaAssitenteSocial.opcaoPolosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Módulo:</th>
				<td>
					<h:selectOneMenu value="#{relatorioFrequenciaAssitenteSocial.idModuloSelecionado}" id="modulo" required="true" >
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{relatorioFrequenciaAssitenteSocial.modulosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Ano - Período:</th>
				<td>
					<h:inputText value="#{relatorioFrequenciaAssitenteSocial.turmaFiltros.anoReferencia}" size="4" maxlength="4" id="ano" onkeypress="return mascara(this,masknumeros);" />
					- <h:inputText value="#{relatorioFrequenciaAssitenteSocial.turmaFiltros.periodoReferencia}" 
  					size="2" maxlength="1" id="periodo" onkeypress="return mascara(this,masknumeros);" />
  					
				</td>
			</tr>
			
			<tr>
				<td colspan="2" style="height: 10px;"></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar Turmas" action="#{ relatorioFrequenciaAssitenteSocial.preencherTurmas }" />
						<h:commandButton value="Cancelar" action="#{relatorioFrequenciaAssitenteSocial.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br /><br />
		<c:if test="${not empty relatorioFrequenciaAssitenteSocial.listaTutorias}">
			
			<a4j:outputPanel id="turmas">
				<div style="width: 90%; margin-left: auto; margin-right: auto;">
					<rich:dataTable value="#{ relatorioFrequenciaAssitenteSocial.listaTutorias }" styleClass="listagem" rowClasses="linhaPar, linhaImpar" var="tutoria" width="100%" rowKeyVar="c">
	
						<f:facet name="caption"><f:verbatim>Turmas encontradas</f:verbatim></f:facet>
			
						<rich:column>
							<f:facet name="header">
								<f:verbatim>
								<a href="#" onclick="selectAllCheckBox();">Todos</a>
								</f:verbatim>
							</f:facet>
							<h:selectBooleanCheckbox value="#{ tutoria.turmaEntrada.dadosTurmaIMD.selecionada }"/>
						</rich:column>
						
						
					
						<rich:column>
							<f:facet name="header"><f:verbatim>Turma</f:verbatim></f:facet>
							<h:outputText value="#{tutoria.turmaEntrada.especializacao.descricao}"/>
						</rich:column>
						<rich:column>
							<f:facet name="header"><f:verbatim>Tutor</f:verbatim></f:facet>
							<h:outputText value="#{tutoria.tutor.pessoa.nome}"/>
						</rich:column>
						<rich:column>
							<f:facet name="header"><f:verbatim>Módulo</f:verbatim></f:facet>
							<h:outputText value="#{tutoria.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao}"/>
						</rich:column>
						<rich:column>
							<f:facet name="header"><f:verbatim>Ano - Período</f:verbatim></f:facet>
							<h:outputText value="#{ tutoria.turmaEntrada.anoReferencia }.#{tutoria.turmaEntrada.periodoReferencia}"/>
						</rich:column>
						<rich:column>
							<f:facet name="header"><f:verbatim>Horário</f:verbatim></f:facet>
							<h:outputText value="#{tutoria.turmaEntrada.dadosTurmaIMD.horario}"/>
						</rich:column>
						
						
						<f:facet name="footer">
						<rich:columnGroup>
							<rich:column style="text-align: center" colspan="6">
								<h:commandButton value="Gerar Relatório" action="#{ relatorioFrequenciaAssitenteSocial.submeterPoloTurmasSemFiltros }" />
								<h:commandButton value="Gerar Relatório com Filtros" action="#{ relatorioFrequenciaAssitenteSocial.submeterPoloTurmas }" />
							</rich:column>
						</rich:columnGroup>
					</f:facet>	
						
						
					
					</rich:dataTable>
				</div>		
					
			</a4j:outputPanel>	
		 	
	 	</c:if>
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>
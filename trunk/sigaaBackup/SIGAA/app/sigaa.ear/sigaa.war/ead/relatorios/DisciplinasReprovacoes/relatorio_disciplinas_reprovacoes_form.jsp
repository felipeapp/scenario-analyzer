<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{ relatorioDisciplinasReprovacoesBean.create }" />
	<h2> <ufrn:subSistema /> &gt; Relat�rio de Reprova��es por Disciplina </h2>
	
	<div class="descricaoOperacao">
		<h4>Caro(a) Usu�rio(a),</h4> <br />
		<p>
			Este relat�rio exibe uma listagem dos discentes reprovados por disciplina de acordo com os crit�rios selecionados.
			� obrigat�rio a sele��o do ano.per�odo e pelo menos uma das outras op��es: curso ou componente.
		</p>
	</div>
	
		<h:form id="anoPeriodoForm">
			<table align="center" class="formulario" width="90%" >
				<caption class="listagem">Dados do Relat�rio</caption>
				<tbody>
				<tr>
					<th class="obrigatorio">Ano-Per�odo:</th>
					<td>
						<h:inputText value="#{relatorioDisciplinasReprovacoesBean.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						- <h:inputText value="#{relatorioDisciplinasReprovacoesBean.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
						<ufrn:help>Ano e per�odo que deseja buscar</ufrn:help>
					</td>
				</tr>
				
				<tr>
					<th>Curso:</th>
					<td>
						<a4j:region>
						<h:selectOneMenu id="curso" value="#{relatorioDisciplinasReprovacoesBean.curso.id}" style="width: 90%;">
							<f:selectItem itemLabel=" -- TODOS -- " itemValue="0" />
							<f:selectItems value="#{curso.allCursosGraduacaoADistanciaCombo}" />
							<a4j:support event="onchange" reRender="componente"></a4j:support>
						</h:selectOneMenu>
						<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/ajax-loader.gif"/>
						</f:facet>
					</a4j:status>
						</a4j:region>
					</td>
				</tr>
				
				<tr>
					<th>Componente:</th>
					<td>
						<h:selectOneMenu id="componente" value="#{relatorioDisciplinasReprovacoesBean.componente.id}" style="width: 90%;">
							<f:selectItem itemLabel=" -- TODOS -- " itemValue="0" />
							<f:selectItems value="#{relatorioDisciplinasReprovacoesBean.allComponentesADistanciaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				</tbody>
				<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Gerar Relat�rio" action="#{relatorioDisciplinasReprovacoesBean.gerarRelatorio}" id="submeterAnoPeriodo" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioDisciplinasReprovacoesBean.cancelar}" id="cancelarAnoPeriodo" />
					</td>
				</tr>
				</tfoot>
			</table>
			<br>
			<center><html:img page="/img/required.gif" style="vertical-align: top;" /> 
				<span class="fontePequena">	Campos de preenchimento obrigat�rio. </span> 
			</center>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

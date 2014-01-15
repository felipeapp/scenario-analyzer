<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.dadosTurma th {
		background: #EEE;
		border-bottom: 1px solid #DDD;
		font-weight: bold;
	}
	tr.dadosTurma td {
		background: #EEE;
		border-bottom: 1px solid #DDD;
	}
	tr.totalAlunos td {
		text-align: center;
	}
	tr.totalAlunos td input {
		color: #292;
		font-weight: bold;
		font-size: 1.3em;
		padding: 3px;
		text-align: center;
	}
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	 
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurmaMedioMBean.descricaoTipo}  &gt; Definir Alunos</h2>
	<h:form id="alunos">

	<table class="formulario" width="80%">
		<caption class="formulario">Defina os discentes a serem transferidos</caption>
		<tr>
			<td colspan="7" class="subFormulario">
				Turma de Origem
			</td>
		</tr>
		<tr>
			<td colspan="6">
				${transferenciaTurmaMedioMBean.turmaSerieOrigem.descricaoCompleta } <br/>
			</td>
			<td><b>Ano:</b> ${transferenciaTurmaMedioMBean.turmaSerieOrigem.ano} </td>
		</tr>
		<tr class="dadosTurma">
			<th colspan="2">Turno:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.turno.descricao}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.qtdMatriculados}</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieOrigem.capacidadeAluno}</td>
		</tr>
		
		<tr>
			<td colspan="7" class="subFormulario">
				Turma de Destino
			</td>
		</tr>
		<tr>
			<td colspan="6">
				${transferenciaTurmaMedioMBean.turmaSerieDestino.descricaoCompleta } <br/>
			</td>
			<td><b>Ano:</b> ${transferenciaTurmaMedioMBean.turmaSerieDestino.ano} </td>
		</tr>
		<tr class="dadosTurma">
			<th colspan="2">Turno:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.turno.descricao}</td>
			<th>Matriculados:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.qtdMatriculados}	</td>
			<th>Capacidade:</th>
			<td>${transferenciaTurmaMedioMBean.turmaSerieDestino.capacidadeAluno}	</td>
		</tr>
		
		<c:if test="${ transferenciaTurmaMedioMBean.automatica }">
			<tr>
				<td colspan="8" class="subFormulario">
					Alunos a transferir
				</td>
			</tr>		
			
			<tr class="totalAlunos">
				<th class="obrigatorio" colspan="4" style="text-align:right" > 
					Número de alunos matriculados a transferir: 
				</th>
				<td>
					<h:inputText value="#{ transferenciaTurmaMedioMBean.qtdMatriculas }" size="3" maxlength="3"  onkeypress="return ApenasNumeros(event);"/>
					
				</td>
			</tr>
			
		</c:if>
		
		<c:if test="${ not transferenciaTurmaMedioMBean.automatica }">
			
			<c:if test="${ not empty transferenciaTurmaMedioMBean.matriculas}">
			<tr><td class="subFormulario" colspan="8"> Alunos matriculados </td>
			<tr><td colspan="8">
			<t:dataTable id="datatableMatriculas" var="matricula" value="#{ transferenciaTurmaMedioMBean.matriculas }" 
				rowClasses="linhaPar, linhaImpar" styleClass="listagem" columnClasses="colCenter,colLeft,colLeft,colLeft"
				headerClass="colCenter" >
				<t:column>
					<f:facet name="header"><f:verbatim><p align="center">Matrícula</p></f:verbatim></f:facet>
					<h:outputText value="#{ matricula.discenteMedio.matricula }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{ matricula.discenteMedio.nome }"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
					<h:outputText value="#{ matricula.descricaoSituacao }"/>
				</t:column>
				<t:column>
					<f:facet name="header"></f:facet>
					<h:selectBooleanCheckbox value="#{ matricula.selected }"/>
				</t:column>
			</t:dataTable>
			</td></tr>
			</c:if>
			
		</c:if>		
		
		<tfoot>
			<tr>
			<td colspan="8">
				<h:commandButton value="Confirmar Transferência" action="#{transferenciaTurmaMedioMBean.efetuarTransferencia}" />
				<h:commandButton value="<< Voltar" action="#{transferenciaTurmaMedioMBean.voltarTurmaDestino}" />
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurmaMedioMBean.cancelar}" />
			</td>
			</tr>
		</tfoot>		
	</table>
</h:form>

<br>
<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
	class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>
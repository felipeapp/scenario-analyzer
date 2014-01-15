<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<%@include file="/portais/discente/menu_discente.jsp"%>
	<h:outputText value="#{ atividadeMonitor.create }"/>
	<h2><ufrn:subSistema /> > Aceitar ou recusar monitoria</h2>
	<h:form>

	<h:inputHidden value="#{atividadeMonitor.confirmButton}"/>

	<table class="formulario" width="100%">
	<caption class="listagem">Aceitar ou recusar participação em projeto de monitoria</caption>

	<tr>
		<td class="subFormulario" colspan="2">Dados do projeto</td>
	</tr>

	<tr>
		<th><b>Ano do Projeto:</b></th>
		<td>
			<h:outputText value="#{ discenteMonitoria.obj.projetoEnsino.ano }" />			
		</td>
	</tr>

	<tr>
		<th><b>Título do Projeto:</b></th>
		<td>
			<h:outputText value="#{ discenteMonitoria.obj.projetoEnsino.titulo }" />
		</td>
	</tr>

    <tr>
        <th><b>Execução do Projeto:</b></th>
        <td>
            <h:outputText value="#{ discenteMonitoria.obj.projetoEnsino.projeto.dataInicio }" /> até
            <h:outputText value="#{ discenteMonitoria.obj.projetoEnsino.projeto.dataFim }" /> 
        </td>
    </tr>

	<tr>
		<th><b>Coordenador(a):</b></th>
		<td>
			<h:outputText value="#{ discenteMonitoria.obj.projetoEnsino.coordenacao.pessoa.nome }"/>
		</td>
	</tr>
	

	<tr>
		<td class="subFormulario" colspan="2">Dados da Prova Seletiva</td>
	</tr>


	<tr>
		<th><b>Título da Prova Seletiva:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.provaSelecao.titulo }" />
		</td>		
	</tr>


	<tr>
		<th><b>Discente:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.discente.matriculaNome }" />
		</td>		
	</tr>


	<tr>
		<th><b>Classificação:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.classificacao }" />º
		</td>		
	</tr>

	<tr>
		<th><b>Nota da Prova:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.notaProva }" />
		</td>		
	</tr>


	<tr>
		<th><b>Nota da Final:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.nota }" />
		</td>		
	</tr>


	<tr>
		<th><b>Situação:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.situacaoDiscenteMonitoria.descricao }" />
		</td>		
	</tr>

	<tr>
		<th><b>Vínculo:</b></th>
		<td>
			 <h:outputText value="#{ discenteMonitoria.obj.tipoVinculo.descricao }" />
		</td>		
	</tr>


	<tr>
		<td class="subFormulario" colspan="2">Lista de Orientadores</td>
	</tr>
		
	<tr>
		<th><b>Orientador(es):</b></th>
		<td>
			
			<table width="100%">
				<c:forEach items="${discenteMonitoria.obj.orientacoes}" var="o" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">	
						<td><c:out value="${o.equipeDocente.servidor.siapeNome}" /></td>
					</tr>
				</c:forEach>
			</table>
			
		</td>
	</tr>


	<tr>
		<td class="subFormulario" colspan="2">Aceitar ou Recusar Participação</td>
	</tr>


	<tr>
		<th><b>Aceitar Monitoria:</b></th> 
		<td> 		
			
			 <h:selectOneMenu id="SelectOneMenuAceita" value="#{discenteMonitoria.obj.selecionado}">
				<f:selectItem itemValue="TRUE" itemLabel="SIM"/>
				<f:selectItem itemValue="FALSE" itemLabel="NÃO"/>
 			 </h:selectOneMenu>				
			<ufrn:help img="/img/ajuda.gif" >Caso não aceite a vaga, por favor, cadastre uma justificativa.</ufrn:help>
		</td>
	</tr>

	<tr>
		<th><b>Justificativa:</b></th> 
		<td>
			<h:inputTextarea rows="3" style="width:98%" id="justificativa" value="#{ discenteMonitoria.obj.observacao }"/>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Confirmar" action="#{discenteMonitoria.aceitarOuRecusarMonitoria}"/>
				<h:commandButton value="Cancelar" action="#{discenteMonitoria.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
	</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Curso > Cadastrar Turmas de Entrada </h2>

<div class="descricaoOperacao">
		Caro coordenador,
		<br><br>
		As turmas de entrada são utilizadas para agrupar um conjunto de alunos que ingressam no curso
		em um mesmo momento. O coordenador pode distribuir o total de vagas do seu curso em várias turmas
		de entrada, de acordo com a maneira mais conveniente para a organização do curso.  
</div>

<f:view>



<h:form prependId="false">
	<table class="formulario" width="60%" align="center">
	<caption>Dados da Turma de Entrada</caption>
	<tr>
		<th><b>Código:</b></th>
		<td align="left">
			<h:outputText value="#{turmaEntrada.obj.codigo}" id="codigoTurmaEntrada" />
		</td>
	</tr>
	<tr>
		<th class="required" width="30%">Data Inicial:</th>
		<td align="left">			
			<t:inputCalendar
				id="dataInicial"
				renderAsPopup="true"
				renderPopupButtonAsImage="true"
				value="#{turmaEntrada.obj.dataInicio}" 
				popupDateFormat="dd/MM/yyyy"
				popupTodayString="Hoje é"
				size="10"
				maxlength="10"
				onkeypress="return(formataData(this,event))">
				<f:converter converterId="convertData"/>
			</t:inputCalendar>			
		</td>
	</tr>
	
	<tr>
		<th class="required">Data Final:</th>
		<td align="left">
			<t:inputCalendar
				id="dataFinal"
				renderAsPopup="true"
				renderPopupButtonAsImage="true"
				value="#{turmaEntrada.obj.dataFim}" 
				popupDateFormat="dd/MM/yyyy"
				popupTodayString="Hoje é"
				size="10"
				maxlength="10"
				onkeypress="return(formataData(this,event))">
				<f:converter converterId="convertData"/>
			</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th class="required">Vagas:</th>
		<td align="left">			
			<h:inputText value="#{turmaEntrada.obj.vagas}"	id="vagasTurmaEntrada"  maxlength="3" size="6" onkeyup="return formatarInteiro(this)"/>
		</td>
	</tr>
	
	<%--
	<tr>
		<th>Curso:</th>
		<td align="left">          		 
          	<h:selectOneMenu value="#{turmaEntrada.obj.cursoLato.id}" style="width:95%;">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{turmaEntrada.allCursosCoordenadorCombo}"/>
			</h:selectOneMenu>	 
          	        	
		</td>
	</tr>
	 --%>
 	<tr>
		<th class="required">Turno:</th>
		<td align="left">          		 
          	<h:selectOneMenu value="#{turmaEntrada.obj.turno.id}" style="width:95%;" id="turnoEnt">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{turmaEntrada.allTurnoAtivosCombo}"/>
			</h:selectOneMenu>	 
          	        	
		</td>
	</tr>
	<tr>
		<th class="required">Tipo Periodicidade Aula:</th>
		<td align="left">			
            <h:selectOneMenu value="#{turmaEntrada.obj.tipoPeriodicidadeAula.id}" style="width:95%;" id="tipoPeriodicidade">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{turmaEntrada.allTiposPeriodicidadeAulaCombo}"/>
			</h:selectOneMenu>           	        	
		</td>
	</tr>
 	<tr>
		<th class="required">Município:</th>
		<td align="left">			
            <h:selectOneMenu value="#{turmaEntrada.obj.municipio.id}" style="width:95%;" id="municipioTurma">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{turmaEntrada.allMunicipiosAtivosCombo}"/>
			</h:selectOneMenu>      	
		</td>
	</tr>
	<tr>
		<th class="required">Campus:</th>
		<td align="left">
			
            <h:selectOneMenu value="#{turmaEntrada.obj.campusIes.id}" style="width:95%;" id="campusIes">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{turmaEntrada.allCampusIesCombo}"/>
			</h:selectOneMenu>       	
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="AdicionarTurmaEntrada" value="#{turmaEntrada.confirmButton}" action="#{turmaEntrada.cadastrar}" />
				<h:commandButton id="CancelarCadastroTurmaEntrada" value="Cancelar" action="#{turmaEntrada.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table> 

<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>



<c:if test="${turmaEntrada.confirmButton == 'Cadastrar'}">
<c:if test="${not empty turmaEntrada.turmasEntrada}">
<br>
	<div class="infoAltRem">	    
	    <h:graphicImage  url="/img/alterar.gif" style="overflow: visible;"/>: Alterar Turma
	    <h:graphicImage  url="/img/delete.gif"  style="overflow: visible;"/>: Remover Turma
	</div>

	    <table class="listagem" width="80%">
		<caption class="listagem">Turmas de Entrada</caption>
	        <thead>
	        <tr>
	        	<td>Código/Cidade</td>
		        <td style="text-align: center;">Data Início</td>
		        <td style="text-align: center;">Data Fim</td>
		        <td style="text-align: right;">Vagas</td>
		        <td></td>
		        <td></td>
		    </tr>
	        </thead>
	        <tbody>
	        <c:forEach items="#{turmaEntrada.turmasEntrada}" var="t">
	            <tr>
					<td>${t.descricao}</td>
	                <td style="text-align: center;"><fmt:formatDate value="${t.dataInicio}"/></td>
	                <td style="text-align: center;"><fmt:formatDate value="${t.dataFim}"/></td>
	                <td style="text-align: right;">${t.vagas}</td>
	                <td width="5%">						
						<h:commandLink action="#{turmaEntrada.preAlterar}" style="border: 0;" id="alterarTurmaEntrada">
							<f:param name="id" value="#{t.id}"/>
							<h:graphicImage url="/img/alterar.gif" title="Alterar Turma"/>
						</h:commandLink>                	
	                </td>
	                <td width="5%">			
						<h:commandLink action="#{turmaEntrada.remover}" style="border: 0;" id="removerT" onclick="return confirm('Atenção! Deseja realmente remover esta Turma de Entrada?');" >
							<f:param name="id" value="#{t.id}"/>
							<h:graphicImage url="/img/delete.gif" title="Remover Turma"/>
						</h:commandLink>                	
	                </td>		                
	            </tr>
	        </c:forEach>
	        </tbody>
	    </table>

</c:if>
</c:if>
 </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

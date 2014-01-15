<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Negar Aviso de Falta </h2>
<f:view>

	<a4j:keepAlive beanName="avisoFaltaHomologada"></a4j:keepAlive>
	<a4j:keepAlive beanName="avisoFalta"></a4j:keepAlive>


	<div class="descricaoOperacao">
		<p>
			Caro Professor,
		</p>	
		<br/>
		<p>
			É necessário informar uma justificativa para <h:outputText value="#{avisoFaltaHomologada.confirmButton}" /> o aviso de falta.
		</p>
	</div>

	<h:form>
		<table width="100%" class="formulario">
			<caption><h:outputText value="#{avisoFaltaHomologada.confirmButton}" /> Aviso de Falta</caption>
			<tr>
				<td>
					<table class="subListagem">
						<caption>Dados do Aviso</caption>
						<thead>
							<tr>
								<th>Nome</th>
								<th width="15%" nowrap="nowrap"><center>Data da Falta</center></th>
								<th width="15%">Turma</th>
								<th>Disciplina</th>
							</tr>			
						</thead>
						<tbody>
							<tr>
								<td>${ avisoFaltaHomologada.obj.dadosAvisoFalta.docenteNome }</td>
								<td><center><fmt:formatDate value="${ avisoFaltaHomologada.obj.dadosAvisoFalta.dataAula }" pattern="dd/MM/yyyy"/></center> </td>
								<td>${ avisoFaltaHomologada.obj.dadosAvisoFalta.turma.codigo }</td>
								<td>${ avisoFaltaHomologada.obj.dadosAvisoFalta.turma.disciplina.codigoNome }</td>
							</tr>
						</tbody>
					</table>				
				</td>
			</tr>
			<tr>
				<td>
					<table class="subFormulario" width="100%">
					<caption>Justificativa</caption>
						<tr>
							<td align="center"><span class="required">Justificativa:</span> <h:inputTextarea rows="5" value="#{avisoFaltaHomologada.obj.motivoNegacao}" style="width: 90%" /></td>
						</tr>
					</table>				
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="#{avisoFaltaHomologada.confirmButton}" action="#{ avisoFaltaHomologada.negarHomologacao }"/>
						<h:commandButton value="Cancelar" action="#{ avisoFaltaHomologada.cancelar }" onclick="#{confirm}"/>					
					</td>
				</tr>
			</tfoot>					
		</table>
	</h:form>
	<br />
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		 <span class="fontePequena">Campos de preenchimento obrigatório. </span> 
		<br>
		<br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Negar Aviso de Falta </h2>
<f:view>

<a4j:keepAlive beanName="faltaHomologada"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Caro Professor,
		</p>	
		<br/>
		<p>
			É necessário informar uma justificativa para negar o aviso de falta de qualquer docente.
		</p>
	</div>

	<h:form>
		<table width="100%" class="formulario">
			<caption>Negar Aviso de Falta</caption>
			<tr>
				<td>
					<table class="subListagem">
						<caption>Dados do Aviso</caption>
						<thead>
							<tr>
								<th>Nome</th>
								<th width="15%" nowrap="nowrap">Data da Falta</th>
								<th width="15%">Turma</th>
								<th>Disciplina</th>
							</tr>			
						</thead>
						<tbody>
							<tr>
								<td>${ faltaHomologada.obj.dadosAusencia.docenteTurma.docenteNome }</td>
								<td><fmt:formatDate value="${ faltaHomologada.obj.dadosAusencia.dataAula }" pattern="dd/MM/yyyy"/> </td>
								<td>${ faltaHomologada.obj.dadosAusencia.docenteTurma.turma.codigo }</td>
								<td>${ faltaHomologada.obj.dadosAusencia.docenteTurma.turma.disciplina.codigoNome }</td>
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
							<td align="center"><h:inputTextarea rows="5" value="#{faltaHomologada.obj.motivoNegacao}" style="width: 90%" /></td>
						</tr>
					</table>				
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Negar" action="#{ faltaHomologada.negarHomologacao }"/>
						<h:commandButton value="Cancelar" action="#{ faltaDocente.cancelar }" onclick="#{confirm}"/>					
					</td>
				</tr>
			</tfoot>					
		</table>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
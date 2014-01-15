<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	
		
	<h2><ufrn:subSistema /> > Seleção do Coordenador do Projeto</h2>
	
	<c:if test="${!projetoMonitoria.selecaoCoordenador}">
		<div class="descricaoOperacao">
			<table width="100%">
				<tr>
					<td>Nesta tela deve ser informado(a) o(a) Coordenador(a) do Projeto de Monitoria.</td>
					<td><%@include file="../ProjetoMonitoria/passos_projeto.jsp"%></td>
				</tr>
			</table>
		</div>
	</c:if>
	
	<h:form id="form">
		<h:inputHidden value="#{projetoMonitoria.selecaoCoordenador}"/>
		<table class="formulario" width="100%" id="formulario">
				<caption class="listagem">Selecione um Docente do Projeto como Coordenador</caption>
                <tr>
                    <th><b>Ano:</b></th>
                    <td>${projetoMonitoria.obj.ano}</td>
                </tr>
			   	<tr>
				   	<th width="20%"><b>Título do Projeto:</b></th>
				   	<td>${projetoMonitoria.obj.titulo}</td>
			   	</tr>
	            <tr>
	                <th><b>Dimensão Acadêmica:</b></th>
			  	   <td>${projetoMonitoria.obj.projetoAssociado ? 'ASSOCIADO' : 'MONITORIA'}</td>
	            </tr>
	            <tr>
	                <th><b>Coordenador(a) atual:</b></th>
	               <td>${projetoMonitoria.obj.projeto.coordenador.pessoa.nome}</td>
	            </tr>
	  	</table>

		<table class="formulario" width="100%">
		   	<tr>
		   		<td class="subFormulario">Lista de docentes do projeto<span class="required">&nbsp;</span></td>		   		
		  	</tr>


			<tr>
				<td colspan="2">
								
					<table id="tabelaDocentes" class="listagem">
						<thead>
							<tr>
								<th></th>
								<th>Docente</th>
								<th>Início da Coordenação</th>
								<th>Fim da Coordenação</th>
								<th>Coordenador</th>
								
							</tr>
						</thead>
					
						<tbody>
						
						<c:forEach items="${projetoMonitoria.equipeDocentes}" var="ed" varStatus="status">
							
							
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
								<td>
									<input type="radio" name="id" value="${ed.servidor.id}" id="radio${ed.servidor.id}"  
										<c:if test="${ed.coordenador }">
											checked="checked"
										</c:if>
									/>
								</td>
								
								<td><label for="radio${ed.servidor.id}">${ed.servidor.nome}</label></td>
								
								<td><fmt:formatDate value="${ed.dataInicioCoordenador}" pattern="dd/MM/yyyy"/></td>
								
								<td><fmt:formatDate value="${ed.dataFimCoordenador}" pattern="dd/MM/yyyy"/></td>
																
								<td  style="font-weight: bold;">
									<c:if test="${ed.coordenador && ed.ativo }">Atual</c:if>
								</td>
								
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Alteração" action="#{projetoMonitoria.submeterSelecaoCoordenador}" id="btConfirmacao"	rendered="#{projetoMonitoria.selecaoCoordenador }"/>
						<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" id="btCancel" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>

		</table>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;" id="obrigatorio"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
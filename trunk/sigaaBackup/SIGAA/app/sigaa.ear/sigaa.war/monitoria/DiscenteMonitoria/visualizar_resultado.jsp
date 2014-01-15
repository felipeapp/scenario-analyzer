<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<div class="descricaoOperacao">
			O sistema s� disponibiliza as notas do usu�rio atual.<br/> N�o � permitido que um usu�rio veja notas de outra pessoa sem as devidas permiss�es. <br/>
			<br/>
			<b>Os crit�rios de desempate s�o na seguinte ordem:</b><br/>
			a) maior nota na prova escrita;<br/>
			b) maior nota no(s) componente(s) curricular(es) de forma��o objeto da sele��o;<br/>
			c) maior �ndice de Rendimento Acad�mico (IRA).<br/>								
	</div>


	<table width="100%" class="tabelaRelatorio">
	   <caption>Resultado da Sele��o de Monitoria</caption>
	
				<tbody>
					<tr><th width="30%"><b>Projeto de Ensino:</b></th><td>${provaSelecao.obj.projetoEnsino.anoTitulo}</td></tr>
					<tr><th><b>T�tulo da Prova:</b></th><td>${provaSelecao.obj.titulo}</td></tr>
					<tr><th><b>Inscri��es at�:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataLimiteIncricao}"/></td></tr>
					<tr><th><b>Data da Prova:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataProva}"/></td></tr>
					<tr><th><b>Vagas p/ Bolsistas:</b></th><td>${provaSelecao.obj.vagasRemuneradas}</td></tr>
					<tr><th><b>Vagas p/ Volunt�rios:</b></th><td>${provaSelecao.obj.vagasNaoRemuneradas}</td></tr>
					<tr><th><b>Situa��o da Prova:</b></th><td>${provaSelecao.obj.situacaoProva.descricao}</td></tr>
					<tr><th><b>Outras Informa��es:</b></th><td>${provaSelecao.obj.informacaoSelecao}</td></tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Lista de Requisitos:</td>
					</tr>
					
					<tr>						
						<td colspan="2">
							<t:dataTable id="dtComponentesProva" value="#{provaSelecao.obj.componentesObrigatorios}" var="compProva" 
									align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"
									rendered="#{not empty provaSelecao.obj.componentesObrigatorios}">
				
									<t:column>
											<f:facet name="header"><f:verbatim><center>Obrigat�rio</center></f:verbatim></f:facet>								
											<center><h:outputText value="#{compProva.obrigatorio ? 'SIM':'N�O'}" /></center>
									</t:column>
									<t:column>
										<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
										<h:outputText value="#{compProva.componenteCurricularMonitoria.disciplina.descricao}" />
									</t:column>			
							</t:dataTable>
						</td>
					</tr>
			</tbody>
	
	</table>
	       
        <br/>
        <br/>       

	<table width="100%" class="tabelaRelatorio">
			    <caption>Classifica��o</caption>

			      <thead>
			      	<tr>
			        	<th style="text-align: right;">Class.</th>
			        	<th>Discente</th>
			        	<th>V�nculo</th>
			        	<th>Situa��o</th>			        	
			        	<th style="text-align: right;">Nota Prova</th>			        	
			        	<th style="text-align: right;">Nota Geral</th>
			        </tr>
			      </thead>


	 	      	<tbody>
				
				<c:set var="lista" value="${provaSelecao.obj.resultadoSelecao}" />


					<c:if test="${empty lista}">
			                  <tr> <td colspan="5" align="center"> <font color="red">N�o h� discentes cadastrados neste projeto.</font> </td></tr>
					</c:if>

					<c:if test="${not empty lista}">

				          	<c:forEach items="${lista}" var="resultado" varStatus="status">
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

					                    <td style="text-align: center;"> ${resultado.classificacaoView}</td>
					                    <td> ${resultado.discente.matriculaNome} </td>
					                    <td> ${resultado.tipoVinculo.descricao} </td>
					                    <td> ${resultado.situacaoDiscenteMonitoria.descricao} </td>					                    
					                    <td style="text-align: right;"> 
					                    	<c:if test="${(!acesso.monitoria) and (!acesso.coordenadorMonitoria) and (acesso.usuario.pessoa.id != resultado.discente.pessoa.id)}">
						        	            ${(resultado.discente.id == usuario.discente.id) ? resultado.notaProva : '<font color=red>--</font>'}
					        	            </c:if> 
					                    	<c:if test="${(acesso.monitoria) or (acesso.coordenadorMonitoria) or (acesso.usuario.pessoa.id == resultado.discente.pessoa.id)}">
						        	            ${resultado.notaProva}
					        	            </c:if> 
					                    </td>
					                    <td style="text-align: right;"> 
					                       	<c:if test="${(!acesso.monitoria) and (!acesso.coordenadorMonitoria) and (acesso.usuario.pessoa.id != resultado.discente.pessoa.id)}">
						        	           ${(resultado.discente.id == usuario.discente.id) ? resultado.nota : '<font color=red>--</font>'}
					        	            </c:if> 
					                    	<c:if test="${(acesso.monitoria) or (acesso.coordenadorMonitoria) or (acesso.usuario.pessoa.id == resultado.discente.pessoa.id)}">
						        	            ${resultado.nota}
					        	            </c:if> 
					                    </td>
					              </tr>
					        </c:forEach>

				</c:if>
			</tbody>						        
		</table>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
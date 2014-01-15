<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2>Quadro Geral dos Projetos de Ensino</h2>
	<h:outputText value="#{comissaoMonitoria.create}"/>

	<c:set var="projetos" value="${projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
	<center><i> Nenhum Projeto a localizado </i></center>
	</c:if>

	<c:if test="${not empty projetos}">

			 <table class="listagem">
			    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
		
			 	<tbody>
		       	<c:forEach items="${projetos}" var="projeto" varStatus="status">
		              <c:set var="corDaLinha" value="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" />
		    	          <tr>
			                    <td>
					              	<table width="100%" class="subFormulario">
		              				    <tr  class="${corDaLinha}">
						                    <td width="15%">Projeto:			</td>	<td> <b>${projeto.titulo} </b></td>
					              		</tr>
		              				    
					              		<tr  class="${corDaLinha}">
						                    <td width="15%">Ano:			</td>	<td> <b>${projeto.ano} </b></td>
					              		</tr>
					              		<tr class="${corDaLinha}">						                    
						                    <td>Centro:			</td>	<td> <b>${projeto.unidade.sigla} - ${projeto.unidade.nome}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">						                    
											<td>Situação:		</td>	<td> <b>${projeto.situacaoProjeto.descricao}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
											<td>Bolsas Remun.:	</td>	<td> <b>${projeto.bolsasConcedidas}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
											<td>Bolsas Não Remun.:</td>	<td> <b>${projeto.bolsasNaoRemuneradas}</b> </td>
					              		</tr>
					              		<tr class="${corDaLinha}">											
								            <td>Coordenador(a):	</td>  	<td> <b>${projeto.coordenacao != null ? projeto.coordenacao.pessoa.nome : ''}</b> </td>
						              	</tr>
						              	<tr class="${corDaLinha}">
						              		<td colspan="7">
								              	<table width="100%">
					              				    <thead>								              		
								              		<tr>
								              			<th width="50%">Lista de Docentes Envolvidos</th>
								              			<%--<th>Lista de Monitores</th>--%>
								              		</tr>
								              		</thead>
								              		
					              				    <tbody>								              		
									              		<tr class="${corDaLinha}">
									              			<td valign="top">
									              			   
									              				<c:forEach items="${projeto.equipeDocentes}" var="ed">
																	<b>${ed.servidor.nome}</b>
																	<c:if test="${ed.coordenador}">
																		<font color="red">(COORDENADOR(A))</font>
																	</c:if>
																	<br/>																	
																</c:forEach>
																
									              			</td>
									              		</tr>
								              		</tbody>
								              	</table>
							              	</td>
						              	<tr>
					              	</table>					              
					              	<hr/>
			              		</td>
			              </tr>
		          </c:forEach>
			 	</tbody>
			
				<tfoot>
					<tr>
						<td colspan="6" align="center">
							<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
						</td>
					</tr>
				</tfoot>
				
		</table>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
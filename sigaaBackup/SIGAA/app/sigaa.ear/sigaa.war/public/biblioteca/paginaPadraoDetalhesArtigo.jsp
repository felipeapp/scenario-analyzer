<%-- Página padrão do sistema para mostrar os detalhes de um artigo --%>
<%-- Mostra todas as informações do artigo selecionado--%>
<%-- Essa página foi feita para ser incluída dentro das outras páginas do sistema --%>


<%-- _artigo_selecionado = o material selecionado, já preenchido com todas as suas informações para evitar n+1 select--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%> 

<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<p:resources />

<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />

<p:dialog header="Detalhes do Artigo" widgetVar="modelPanelDetalhesArtigo" modal="true" width="800" height="500" rendered="#{_artigo_selecionado != null  }">

	<table class="visualizacao" style="margin-bottom: 10px; width: 100%"> 
		
			<tr>
				<td colspan="4" style="text-align:center; font-weight: bold;"> ${_assinatura_artigo_selecionado.titulo}</td>
			</tr>
		
			<tr>
			
				<th >Biblioteca:</th>
				<td colspan="3"> ${_fasciculo_artigo_selecionado.biblioteca.descricao}</td>
			
			</tr>
			<tr>
			
				<th >Código de Barras:</th>
				<td colspan="3"> ${_fasciculo_artigo_selecionado.codigoBarras}</td>
			
			</tr>
			<tr>
				
				<th >Localização:</th>	
				<td style="color:#D99C00;"> ${_fasciculo_artigo_selecionado.numeroChamada}</td>	
				
				<th>Situação:</th>
				<c:if test="${_fasciculo_artigo_selecionado.disponivel}">
					<td style="color:green"> ${_fasciculo_artigo_selecionado.situacao.descricao}</td>
				</c:if>
				
				<c:if test="${! _fasciculo_artigo_selecionado.disponivel && ! _fasciculo_artigo_selecionado.emprestado}">
					<td> ${_fasciculo_artigo_selecionado.situacao.descricao}</td>
				</c:if>
				
				<c:if test="${_fasciculo_artigo_selecionado.emprestado}"> 
					<td style="color:red"> ${_fasciculo_artigo_selecionado.situacao.descricao}</td>
				</c:if>
			
			</tr>
			<tr>
				
				<th style="width: 20%;"> Ano Cronológico: </th>
				<td style="width: 30%;"> ${_fasciculo_artigo_selecionado.anoCronologico} </td>
			
				<th style="width: 20%;"> Ano: </th>	
				<td style="width: 30%;"> ${_fasciculo_artigo_selecionado.ano} </td>
				
			</tr>
			
			<tr>
			
				<th> Dia/Mês: </th>	
				<td> ${_fasciculo_artigo_selecionado.diaMes} </td>
				
				<th> Volume: </th>	
				<td> ${_fasciculo_artigo_selecionado.volume} </td>
				
			</tr>
			
			<tr>	
			
				
				
				<th > Número: </th>
				<td> ${_fasciculo_artigo_selecionado.numero} </td>
				
				<th > Edição: </th>
				<td> ${_fasciculo_artigo_selecionado.edicao} </td>
							
			</tr>
			
			<tr>
				<td colspan="4" style="height: 30px;">  </td>
			</tr>
			
			<tr>
				<th> Autores Secundários: </th>	
				<td colspan="3">
					<c:if test="${ fn:length(_artigo_selecionado.autoresSecundarios) > 0}">
						<table width="100%" id="tabelaInterna">
							<c:forEach var="autorSecundario" items="#{_artigo_selecionado.autoresSecundariosFormatados}">
								<tr>
									<td style="background-color: transparent;">
										${autorSecundario}
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th> Intervalo de Páginas: </th>	
				<td colspan="3">
					${_artigo_selecionado.intervaloPaginas}
				</td>
			</tr>
			
			<tr>
				<th>Local de Publicação: </th>	
				<td colspan="3">
					<c:if test="${ fn:length( _artigo_selecionado.locaisPublicacaoFormatados ) > 0 }">
					<table width="100%" id="tabelaInterna">
					<c:forEach items="${_artigo_selecionado.locaisPublicacaoFormatados}" var="local">
						<tr>
							<td style="background-color: transparent;">
								${local}
							</td>
						</tr>
					</c:forEach>
					</table>
				</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Editora: </th>	
				<td colspan="3">
					<c:if test="${ fn:length( _artigo_selecionado.editorasFormatadas) > 0 }">
						<table width="100%" id="tabelaInterna">
						<c:forEach items="${_artigo_selecionado.editorasFormatadas}" var="editora">
							<tr>
								<td style="background-color: transparent;">
									${editora}
								</td>
							</tr>
						</c:forEach>
						</table>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Ano: </th>	
				<td colspan="3">
					<c:if test="${ fn:length( detalhesArtigoMBean.obj.anosFormatados) > 0 }">
						<table width="100%" id="tabelaInterna">
							<c:forEach items="${_artigo_selecionado.anosFormatados}" var="ano">
								<tr>
									<td style="background-color: transparent;">
											${ano}
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
				</td>
			</tr>
			
		
			
			
			<tr>
				<th> Resumo: </th>	
				<td colspan="3">
					${_artigo_selecionado.resumo}
				</td>
			</tr>
			
			<%-- 
			
			<tr>
				<td colspan="16">
					
					
					<table class="subFormulario"> 
		
						<caption style="text-align: center"> Informações do Artigo </caption>
							
						<thead>
							<tr align="center">
								<th width="30%" style="text-align: left">Título</th>
								<th width="30%" style="text-align: left">Autores Secundários</th>
								<th width="10%"  style="text-align: left">Intervalo de Páginas</th>
							</tr>
						</thead>
						
						<tbody>
						
							<tr>
								<td> </td>
								<td>
									
									<c:if test="${ not empty _artigo_selecionado.autoresSecundarios }">
										<p style="margin-top: 5px;">
										<span style="font-weight: bold;">Autores Secundários:</span>
										<c:forEach var="autorSecundario" items="#{_artigo_selecionado.autoresSecundariosFormatados}">
											<br/> <h:outputText value="#{autorSecundario}" />
										</c:forEach>
										</p>
									</c:if>
								</td>
								<td> ${_artigo_selecionado.intervaloPaginas}</td>
								<td> 
									<table width="100%" id="tabelaInterna">
									<c:if test="${ fn:length( detalhesArtigoMBean.obj.locaisPublicacaoFormatados ) > 0 }">
										<c:forEach items="${detalhesArtigoMBean.obj.locaisPublicacaoFormatados}" var="local">
											<tr>
												<td style="background-color: transparent;">
													${local}
												</td>
											</tr>
										</c:forEach>
									</c:if>
								</table>
								</td>
							</tr>
							
							<tr>
								<td colspan="16">
									<table class="subFormulario" width="100%">
										<thead>
											<tr align="center">
												<th style="text-align: left">Resumo</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td> ${_artigo_selecionado.resumo}</td>
											</tr>	
										</tbody>
									</table>
								<td>
							</tr>
				
						</tbody>
				
					</table>
					
				</td>
			</tr> --%>
		
		
	</table>

</p:dialog>
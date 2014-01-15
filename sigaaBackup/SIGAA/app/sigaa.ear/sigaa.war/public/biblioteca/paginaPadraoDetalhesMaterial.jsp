
<%-- P�gina padr�o do sistema para mostrar os detalhes de um material --%>
<%-- Mostra todas as informa��es al�m das reservas e quantidade de empr�stimo no material selecionado --%>
<%-- Essa p�gina foi feita para ser inclu�da dentro das outras p�ginas do sistema --%>


<%-- _material_selecionado = o material selecionado, j� preenchido com todas as suas informa��es para evitar n+1 select--%>
<%-- _reservas_do_material_selecionado = a listagem das reservas existentes para o t�tulo do material, j� preenchido com todas as suas informa��es para evitar n+1 select --%>
<%-- _qtd_emprestimos_materail_selecionado =  a quantidade de empr�stimos do material passado --%>
<%-- _is_fasciculo =  se o material passado � fasc�culo ou n�o. --%>

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


<style type="text/css">

table.tabelaInterna  tbody{
	background-color: transparent;
}

</style>


	<p:dialog header="Detalhes do Material" widgetVar="modelPanelDetalhes" modal="true" width="800" height="500" rendered="#{_material_selecionado != null  }"> 	
		
		<table  id="dadosMaterial"  class="visualizacao" style="border: 0; font-size: 90%; width: 100%;">
			
			<tr>
				<th style="width: 40%; text-align: left;">C�digo de Barras:</th>
				<td colspan="3">${_material_selecionado.codigoBarras}
					<c:if test="${! _is_fasciculo  }"> 
						<c:if test="${_material_selecionado.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> 
					</c:if>	
					<c:if test="${_is_fasciculo  }"> 
						<c:if test="${_material_selecionado.suplemento}"> <span style="font-style: italic;">(suplemento)</span> </c:if> 
					</c:if>	  
				</td>
				
			</tr>
			
			<c:if test="${! _is_fasciculo  }"> 
				<tr>
					<th style="text-align: left;">N�mero do Patrim�nio:</th>
					<c:if test="${not empty _material_selecionado.numeroPatrimonio}">	
						<td colspan="3"> ${_material_selecionado.numeroPatrimonio}</td>
					</c:if>
					<c:if test="${empty _material_selecionado.numeroPatrimonio}">
						<td colspan="3"> <span style="font-style: italic;"> Material n�o tombado </span> </td>
					</c:if>	
				</tr>
			</c:if>
			
			<tr>
				<th style="text-align: left;">N�mero de Chamada ( Localiza��o ) :</th>
				<td colspan="3" style="color: #CD853F;">${_material_selecionado.numeroChamada}</td>
			</tr>
			
			<tr>
				<th style="text-align: left;">Segunda Localiza��o:</th>
				<td colspan="3" style="color: #CD853F;">${_material_selecionado.segundaLocalizacao}</td>
			</tr>
			
			<tr>
				<th style="text-align: left;">Biblioteca:</th>
				<td colspan="3">${_material_selecionado.biblioteca.descricao}</td>
			</tr>
			
			<tr>
				<th style="text-align: left;">Cole��o:</th>
				<td colspan="3">${_material_selecionado.colecao.descricao}</td>
			</tr>
			
			<tr>
				<th style="text-align: left;">Situa��o:</th>
				
				<c:if test="${_material_selecionado.disponivel}"> 
					<td colspan="3" style="color:green"> ${_material_selecionado.situacao.descricao}</td>
				</c:if>
				<c:if test="${! _material_selecionado.disponivel && ! _material_selecionado.emprestado}"> 
					<td colspan="3" > ${_material_selecionado.situacao.descricao}</td>
				</c:if>
				<c:if test="${_material_selecionado.emprestado}"> 
					<td colspan="3" style="color:red"> ${_material_selecionado.situacao.descricao}</td>
				</c:if>
			</tr>
			
			<tr>
				<th style="text-align: left;">Status:</th>
				<td colspan="3">${_material_selecionado.status.descricao}</td>
			</tr>
			
			<tr>
				<th style="text-align: left;">Tipo de Material:</th>
				<td colspan="3">${_material_selecionado.tipoMaterial.descricao}</td>
			</tr>
			
			
			<c:if test="${fn:length(_material_selecionado.formasDocumento)  > 0}">
				<tr>
					<th colspan="2" style="text-align: left;">Formas do Documento:</th>
				</tr>
				
				<c:forEach var="formaDocumento" items="#{_material_selecionado.formasDocumento}">
					<tr>
						<td colspan="1"></td> 
						<td colspan="1">${formaDocumento.denominacao}</td>
					</tr>
				</c:forEach>
			
			</c:if>
			
			<c:if test="${_is_fasciculo  }"> 
			
				<tr>
					<th style="text-align: left;">Assinatura:</th>
					<td colspan="3">${_assinatura_do_material.codigoTitulo}</td>
				</tr>
			
				<tr>
					<th style="text-align: left;">Ano Cronol�gico:</th>
					<td colspan="3">${_material_selecionado.anoCronologico}</td>
				</tr>
				
				<tr>
					<th style="text-align: left;">Dia/M�s:</th>
					<td colspan="3">${_material_selecionado.diaMes}</td>
				</tr>
				
				<tr>
					<th style="text-align: left;">Ano:</th>
					<td colspan="3">${_material_selecionado.ano}</td>
				</tr>
				
				<tr>
					<th style="text-align: left;">Volume:</th>
					<td colspan="3">${_material_selecionado.volume}</td>
				</tr>
				
				<tr>
					<th style="text-align: left;">N�mero:</th>
					<td colspan="3">${_material_selecionado.numero}</td>
				</tr>
				
				<tr>
					<th style="text-align: left;">Edi��o:</th>
					<td colspan="3">${_material_selecionado.edicao}</td>
				</tr>
			
			</c:if>
			
			<c:if test="${! _is_fasciculo  }"> 
			
				<tr>
					<th style="text-align: left;" >N�mero do Volume:</th>
					<td style="text-align: left;" colspan="3">
						<c:if test="${_material_selecionado.numeroVolume != null}">
							${_material_selecionado.numeroVolume}
						</c:if>
						<c:if test="${_material_selecionado.numeroVolume == null}">
							�nico 
						</c:if>
					</td>
				</tr>
				
				
				
				<tr>	
					<th style="text-align: left;" >Tomo:</th>
					<td style="text-align: left;" colspan="3">
						<c:if test="${_material_selecionado.tomo != null}">
							${_material_selecionado.tomo}
						</c:if>
						<c:if test="${_material_selecionado.tomo == null}">
							�nico 
						</c:if>
					</td>
				</tr>
			
			</c:if>
			
			<c:if test="${not empty _material_selecionado.notaUsuario}">	
				<tr>
					<th colspan="2" style="text-align: left;">Nota ao Usu�rio:</th>
				</tr>
				<tr>
					<td colspan="2" style="text-align: left;">${_material_selecionado.notaUsuario}</td>
				</tr>
			</c:if>
			
			<c:if test="${not empty _material_selecionado.notaGeral}">
				<tr>
					<th colspan="2" style="text-align: left;">Nota Geral:</th>
				</tr>
				
				<tr>
					<td colspan="2" style="text-align: left;" >${_material_selecionado.notaGeral}</td>
				</tr>
			</c:if>
			
			<c:if test="${! _is_fasciculo  }"> 
			
				<c:if test="${not empty _material_selecionado.notaTeseDissertacao}">
					<tr>
						<th colspan="2" style="text-align: left;">Nota de Tese e Disserta��o:</th>
					</tr>
					
					<tr>
						<td colspan="2" style="text-align: left;">${_material_selecionado.notaTeseDissertacao}</td>
					</tr>
				</c:if>
				
				<c:if test="${not empty _material_selecionado.notaConteudo}">
					<tr>
						<th colspan="2" style="text-align: left;">Nota de Conte�do:</th>
					</tr>
					<tr>
						<td colspan="2" style="text-align: left;">${_material_selecionado.notaConteudo}</td>
					</tr>
				</c:if>
			</c:if>
			
			<c:if test="${_is_fasciculo  }"> 
				<c:if test="${not empty _material_selecionado.descricaoSuplemento}">
					<tr style="font-style: italic;">
						<th style="text-align: left;">Descri��o do Suplemento: </th> <td> ${_material_selecionado.descricaoSuplemento}<td>
					</tr>
				</c:if>
				
				<c:if test="${_material_selecionado.quantidadeArtigos > 0}">
												 
					<tr>
						<th colspan="2" style="text-align: left;">Quantidade de Artigos do Fasc�culo:</th>
						<td colspan="2" style="text-align: left;">${_material_selecionado.quantidadeArtigos}</td>
					</tr>
					
				</c:if>
				
			</c:if>	
			
			<c:if test="${_material_selecionado.dadoBaixa}">
				<tr>
					<th> Motivo da Baixa:</th>
					<td colspan="3"> ${_material_selecionado.motivoBaixa}</td>
				</tr>
				
				<tr>
					<th colspan="1" style="width: 25%;"> Baixado Por:</th>
					<td colspan="1" style="width: 25%;"> ${_material_selecionado.registroUltimaAtualizacao.usuario.pessoa.nome}</td>
					<th colspan="1" style="width: 25%;"> Data da Baixa:</th>
					<td colspan="1" style="width: 25%;"> <ufrn:format type="dataHora" valor="${_material_selecionado.dataUltimaAtualizacao}" /> </td>
				</tr>
				
				<tr style="height: 30px;">
					<td colspan="4"> </td>
				</tr>
			</c:if>
			
		</table>
		
		<c:if test="${_is_mostrar_informacoes_titulo}">
			<table id="tableTitulos" class="visualizacao"  style="border: 1px; font-size: 90%; width: 100%; margin-top: 20px; margin-bottom: 20px;">
				<caption>T�tulo do Material</caption>
				
				<c:if test="${! _material_selecionado.dadoBaixa}"> 
					<tr>
						<th>N� do Sistema:</th>
						<td>${_titulo_do_material.numeroDoSistema}</td>
					</tr>			
			
					<tr>
						<th>T�tulo:</th>
						<td>${_titulo_do_material.titulo}</td>
					</tr>			
			
					<tr>
						<th>Autor:</th>
						<td>${_titulo_do_material.autor}</td>
					</tr>
			
					<tr>
						<th>Ano:</th>
						<td>
							<table width="100%" class="tabelaInterna">
								<c:forEach items="${_titulo_do_material.anosFormatados}" var="ano">
								<tr>
									<td>
										${ano}
									</td>
								</tr>
								</c:forEach>
							</table>
						</td>
					</tr>			
			
					<tr>	
						<th>Edi��o:</th>
						<td>
							<table width="100%" class="tabelaInterna">
								<c:forEach items="${_titulo_do_material.edicoesFormatadas}" var="edicao">
								<tr>
									<td>
										${edicao}
									</td>
								</tr>
								</c:forEach>
							</table>
						</td>
					</tr>			
				</c:if>
			
				<c:if test="${_material_selecionado.dadoBaixa}">
					
					<tr>
						<td style="background-color: transparent;" width="75%" colspan="4"> ${_material_selecionado.informacoesTituloMaterialBaixado}</td>
					</tr>
					
					<tr style="height: 30px;">
						<td colspan="4"> </td>
					</tr>
				
				</c:if>
				
				
			</table>
		</c:if>
		
		
		<c:if test="${_is_fasciculo  }"> 
		
			<table id="tableArtigos" class="visualizacao" style="border: 1px; font-size: 90%; width: 100%; margin-top: 20px; margin-bottom: 20px;">
				<caption>Artigos do Fasc�culo</caption>
				
				<thead>
					<th style="width:25%; text-align: left;">Autor</th>
					<th style="width:25%; text-align: left;" >T�tulo</th>
					<th style="width:40%; text-align: left;">Palavras-Chave</th>
					<th style="width:10%; text-align: left;">Intervalo de P�ginas</th>
				</thead>
				
				<tbody>
				<c:if test="${_artigos_do_fasciculo_selecionado != null}">
					<c:forEach var="artigo" items="#{_artigos_do_fasciculo_selecionado}" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							
							<td style="text-align: left;">
								${artigo.autor}
							</td>
							
							<td style="text-align: left;">
								${artigo.titulo}
							</td>
							
							<td style="text-align: left;">
								<table width="100%" class="tabelaInterna">
									<c:forEach items="${artigo.assuntosFormatados}" var="assunto">
									<tr>
										<td>
											${assunto}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
							<td style="text-align: left;">
								${artigo.intervaloPaginas}
							</td>
						</tr>
					</c:forEach>
				</c:if>	
				</tbody>
				
			</table>
		</c:if>	
			
			
		<table  id="tableReservas"  class="visualizacao" style="border: 1px; font-size: 90%; width: 100%; margin-top: 20px; margin-bottom: 20px;">
			<caption>Reservas do T�tulo do Material</caption>
			
			<thead>
				<th style="width:15%; text-align: left;" >Data da Solicita��o</th>
				<th style="width:60%; text-align: left;">Usu�rio Solicitante</th>
				<th style="width: 10%; text-align: left;">Status</th>
				<th style="width: 14%; text-align: left;">Previs�o</th>
			</thead>
			
			<tbody>
				<c:if test="${_reservas_do_material_selecionado != null}">
					<c:forEach var="reserva" items="#{_reservas_do_material_selecionado}" varStatus="status"> 
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"> 
						
							<td style="text-align: left;">
								<ufrn:format type="dataHora" valor="${reserva.dataSolicitacao}" /> 
							</td>
							<td style="text-align: left;">
								${reserva.usuarioReserva.pessoa.nome}
							</td>
							<td style="text-align: left;">
								${reserva.status.descricao}
							</td>
							<c:if test="${empty reserva.prazoRetiradaMaterial}">
								<td style="text-align: left;">
									<ufrn:format type="data" valor="${reserva.dataPrevisaoRetiradaMaterial}" />
								</td>
							</c:if>
							<c:if test="${not empty reserva.prazoRetiradaMaterial}">
								<td style="text-align: left;">
									<ufrn:format type="data" valor="${reserva.prazoRetiradaMaterial}" />
								</td>
							</c:if>
						</tr> 
				
					</c:forEach>
				</c:if>
			</tbody>
		</table>
		
		<table  id="tableEmprestimos"  class="visualizacao" style="border: 1px; font-size: 90%; width: 100%; margin-top: 20px; margin-bottom: 20px;">
			<caption>Empr�stimos do Material</caption>
			
			<tr> 
				<th style="width: 50%;">Quantidade de Empr�stimos desse material:</th> 
				<td style="width: 50%; text-align: center;">${_qtd_emprestimos_materail_selecionado}</td> 
			</tr>
		</table>
				
		<table class="formulario" width="100%" style="border: 0; font-size: 90%;">
			
			<tfoot>
				<tr>
					<td colspan="2">
					 <h:panelGroup>
						 <a onclick="modelPanelDetalhes.hide();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
							Fechar
						 </a>
           			</h:panelGroup>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</p:dialog>

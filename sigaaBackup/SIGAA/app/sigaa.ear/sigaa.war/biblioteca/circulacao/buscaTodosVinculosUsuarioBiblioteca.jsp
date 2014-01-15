<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<jsp:useBean id="vinculoUsuarioBibliotecaDao" class="br.ufrn.sigaa.arq.dao.biblioteca.VinculosUsuarioBibliotecaDao" scope="request"/>

<%-- P�gina de busca onde mostra todos os v�nculos que o usu�rio possui, usada para emitir o documento de quista��o.     --%>
<style>
table.listagem tr.nomePessoa td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<h:form id="formBuscaVinculos">
	
		

		<h2> <ufrn:subSistema /> &gt; Verificar V�nculos do Usu�rio</h2>
		
		<div class="descricaoOperacao">
				<p> P�gina na qual � poss�vel verificar todos os v�nculos que uma determinada pessoa possui no sistema. </p>
				<p>	Para cada v�nculo � mostrado se ele est� ativo para empr�stimos ou n�o.</p>
		</div>
		
		<table class="formulario" style="width:60%;">
			<caption>Informe os crit�rios de busca</caption>
		
			<tbody>
				
				<tr>
					<th><label class="required">Nome:</label></th>
					<td>
						<h:inputText value="#{buscaUsuarioBibliotecaMBean.nomeUsuario}" id="nomeUsuario" maxlength="50" size="70" onkeyup="CAPS(this)"/>
					</td>
				</tr>
			</tbody>
		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cmdBuscarVinculos" action="#{buscaUsuarioBibliotecaMBean.buscarTodosVinculosUsuario}" value="Buscar"  />
						<h:commandButton id="cmdCacelarBuscaVinculos" action="#{buscaUsuarioBibliotecaMBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	
	
	
		<%--   Lista com os resultados da busca    --%> 
		<c:if test="${not empty buscaUsuarioBibliotecaMBean.infoPessoas}">
		<br /><br />
			<table class="listagem" style="width:100%">
				<caption>V�nculos Encontrados  (   ${fn:length(buscaUsuarioBibliotecaMBean.infoPessoas)}  )</caption>
				
				<thead>
					<tr>
						<th style="text-align:center;">CPF/Passaporte</th>
						<th style="text-align:center;">Data de Nascimento</th>
						<th>V�nculo</th>
						<th>Status</th>
							<%-- <th style="width:20px;"></th> --%>
					</tr>
				</thead>
				
				<c:set var="filtroNomePessoa" value="" scope="request" />
				<c:forEach var="p" items="#{buscaUsuarioBibliotecaMBean.infoPessoas}" varStatus="status">
					
					<c:if test="${ filtroNomePessoa != p[vinculoUsuarioBibliotecaDao.posicaoNomePessoa]}">
						<c:set var="filtroNomePessoa" value="${p[vinculoUsuarioBibliotecaDao.posicaoNomePessoa]}" scope="request" />
						<tr  class="nomePessoa">
							<td colspan="4">${p[vinculoUsuarioBibliotecaDao.posicaoNomePessoa]}</td>
						</tr>
					</c:if>
					
					<tr>
						<c:if test="${p[vinculoUsuarioBibliotecaDao.posicaoCpf] != null}">
							<td style="text-align:center;">
								${p[vinculoUsuarioBibliotecaDao.posicaoCpf]} (CPF)
							</td>
						</c:if>
						
						<c:if test="${p[vinculoUsuarioBibliotecaDao.posicaoCpf] == null }">
							
							<c:if test="${p[vinculoUsuarioBibliotecaDao.posicaoPassaporte] != null}">
								<td style="text-align:center;">
									${p[vinculoUsuarioBibliotecaDao.posicaoPassaporte]} (Passaporte)
								</td>
							</c:if>
							<c:if test="${p[vinculoUsuarioBibliotecaDao.posicaoPassaporte] == null}">
								<td style="text-align:center;">
									---
								</td>
							</c:if>
						</c:if>
						
						<td style="text-align:center;">
							<ufrn:format type="data" valor="${p[vinculoUsuarioBibliotecaDao.posicaoDataNascimento]}" />
						</td>
						
						<%-- Descri��o do tipo do v�nculo--%>
						<td>
							${p[vinculoUsuarioBibliotecaDao.posicaoDescricaoVinculo]}
						</td>
						
						<%-- Status --%>
						<td>
							${p[vinculoUsuarioBibliotecaDao.posicaoDescricaoStatus]}
						</td>
						
					</tr>
				</c:forEach>
			</table>
		</c:if>

		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

	</h:form>
	
	
	<script type="text/javascript">

		focar();

		function focar(){
			$('formBuscaVinculos:nomeUsuario').focus();
		}
		
		
	</script>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Publicação de Edital
</h2>

<c:if test="${ param['dispatch'] != 'remove' }">
	<div class="infoAltRem">
		<html:img page="/img/adicionar.gif" style="overflow: visible;"/>
		<html:link action="/pesquisa/cadastroEditalPesquisa?dispatch=edit&aba=projetos">Publicar Novo Edital</html:link>
		<img src="/shared/img/icones/download.png" border="0" style="overflow: visible;"/>
		: Baixar Arquivo Anexo do Edital
	</div>
</c:if>

<table class="visualizacao" width="75%">
       <caption>Dados do Edital</caption>
       <tbody>
       <tr>
           <th width="30%" >Ano do Edital:</th>
           <td> ${objeto.ano} </td>
       </tr>
       <tr>
           <th width="30%" >Descrição:</th>
           <td> ${objeto.descricao} </td>
       </tr>
       <tr>
           <th>Período de Submissões:</th>
           <td>
				<ufrn:format type="data" name="objeto" property="inicioSubmissao" /> a <ufrn:format type="data" name="objeto" property="fimSubmissao" />
		   </td>
       </tr>
       <tr>
           <th>Titulação mínima para solicitação de cotas:</th>
           <td>
				${objeto.titulacaoMinimaCotasDescricao}
		   </td>
       </tr>
		<tr>
			<td colspan="2" class="subFormulario"> Parâmetros da Distribuição de Cotas </td>
		</tr>
		<tr>
			<th> FPPI Mínimo: </th>
			<td> <ufrn:format type="valor" name="objeto" property="fppiMinimo" /></td>
		</tr>
		<c:if test="${ param['dispatch'] != 'remove' }">
	       <tr>
	           <th>Arquivo do Edital:</th>
	           <td>
	           		<a href="/sigaa/verProducao?idProducao=${objeto.idArquivo}&key=${ sf:generateArquivoKey(objeto.idArquivo) }" target="_blank">
					  	<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo Anexo do Edital" title="Baixar Arquivo Anexo do Edital" />
					</a> 
	           </td>
	       </tr>
       </c:if>
		<c:if test="${not empty objeto.cotas}">
			<tr>
				<td colspan="2" class="subFormulario"> Cotas distribuídas </td>
			</tr>
			<tr><td colspan="2">
				<table class="listagem">
	        		<thead>
	        			<tr>
	        				<th style="text-align: left">Tipo da bolsa</th>
	        				<th style="text-align: left">Quantidade</th>
	        			</tr>
	        		</thead>
	        		<c:set var="totalCotas" value="0"/>
	        		<c:forEach items="${objeto.cotas}" var="cota" varStatus="row">
	        			<tr>
	        				<td>${cota.tipoBolsa.descricaoResumida}</td>
	        				<td>${cota.quantidade}</td>
	        				<c:set var="totalCotas" value="${totalCotas + cota.quantidade}"/>
	        			</tr>
	        		</c:forEach>
	        		<tfoot>
	        			<tr>
	        				<th style="text-align: left">Total</th>
	        				<th style="text-align: left">${totalCotas}</th>
	        			</tr>
	        		</tfoot>
	        	</table>
			</td></tr>
		</c:if>
		<c:if test="${ param['dispatch'] == 'remove' }">
			<tfoot>
				<tr>
					<td align="center" colspan="2">
						<html:form action="/pesquisa/cadastroEditalPesquisa" method="post">
							<input type="hidden" name="id" value="${objeto.id}">
							<input type="hidden" name="confirm" value="true">
							<html:button dispatch="remove">Remover</html:button>
							<html:button dispatch="cancelar">Cancelar</html:button>
						</html:form>
					</td>
				</tr>
			</tfoot>
		</c:if>
</table>

<c:if test="${not empty visualizacao and param['dispatch'] != 'remove'}">
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
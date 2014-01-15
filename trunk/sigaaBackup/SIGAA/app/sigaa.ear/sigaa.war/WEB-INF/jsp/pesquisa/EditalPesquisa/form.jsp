<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Publicação de Edital
</h2>

<html:form action="/pesquisa/cadastroEditalPesquisa"
	method="post"
	styleId="form"
	enctype="multipart/form-data"
	focus="obj.descricao">

	<html:hidden property="obj.id" />

	<table class="formulario" width="80%">
        <caption>Dados do Edital</caption>
        <tbody>
		<tr>
            <th>Edital Associado:</th>
            <td>
                <html:select property="obj.edital.id" disabled="${formEditalPesquisa.obj.id > 0}" 
                	onchange="document.getElementById('dispatch').value = 'changeEditalAssociado'; submit();">
                	<html:option value="-1">NENHUM</html:option>
                	<html:options collection="editaisAssociados" property="id" labelProperty="descricao"/>
                </html:select>
            </td>
        </tr>
        <tr>
            <th class="required" width="30%">Ano do Edital:</th>
            <td>
            	<c:choose>
            		<c:when test="${not formEditalPesquisa.editalAssociado}">
		            	<html:text property="obj.ano" size="4" maxlength="4" onkeyup="formatarInteiro(this);" /> 
            		</c:when>
            		<c:otherwise>
            			<strong>${ formEditalPesquisa.obj.ano }</strong>
            		</c:otherwise>
            	</c:choose> 
            </td>
        </tr>
        <tr>
            <th class="required">Descrição:</th>
            <td>
            	<c:choose>
            		<c:when test="${not formEditalPesquisa.editalAssociado}">
		            	<html:text property="obj.descricao" size="60" maxlength="255"/> 
            		</c:when>
            		<c:otherwise>
            			<strong>${ formEditalPesquisa.obj.descricao }</strong>
            		</c:otherwise>
            	</c:choose>  
            </td>
        </tr>
        <c:if test="${not formEditalPesquisa.editalAssociado}">
	        <tr>
	            <th class="required">Arquivo do Edital:</th>
	            <td> <html:file property="arquivoEdital" size="55"></html:file> </td>
	        </tr>
        </c:if>
        <tr>
            <th class="required">Período de Submissões:</th>
            <td>
                <c:choose>
            		<c:when test="${not formEditalPesquisa.editalAssociado}">
		                <ufrn:calendar property="inicioSubmissao"/> a <ufrn:calendar property="fimSubmissao"/>
            		</c:when>
            		<c:otherwise>
            			<strong>
            				<ufrn:format type="data" valor="${formEditalPesquisa.obj.inicioSubmissao}" /> a
							<ufrn:format type="data" valor="${formEditalPesquisa.obj.fimSubmissao}" />
						</strong>
            		</c:otherwise>
            	</c:choose>
            </td>
        </tr>
		<tr>
            <th class="required">Titulação mínima para a solicitação de cotas:</th>
            <td>
                <html:select property="obj.titulacaoMinimaCotas">
                	<html:option value="-1">-- SELECIONE --</html:option>
                	<html:optionsCollection property="obj.titulacoes" label="value" value="key"/>
                </html:select>
            </td>
        </tr>
        <tr>
            <th class="required">Cota:</th>
            <td>
                <html:select property="obj.cota.id">
                	<html:options collection="cotas" property="id" labelProperty="descricao"/>
                </html:select>
            </td>
        </tr>
        <tr>
            <th class="required">Categoria:</th>
            <td>
                <html:select property="obj.categoria.id">
                	<html:option value="-1">-- SELECIONE --</html:option>
                	<html:options collection="categorias" property="id" labelProperty="denominacao"/>
                </html:select>
            </td>
        </tr>
        <tr>
        	<th class="required">Edital para voluntários?</th>
        	<td>
				<html:radio property="obj.voluntario" value="true" style="simVoluntario"/> <label for="simVoluntario"> Sim </label>
				<html:radio property="obj.voluntario" value="false" style="naoVoluntario"/> <label for="naoVoluntario"> Não </label>
        	</td>
        </tr>
		<tr>
			<td colspan="2" class="subFormulario"> Parâmetros da Distribuição de Cotas </td>
		</tr>
        <tr>
        	<th class="required">Distribuição de Cotas de Bolsas?</th>
        	<td>
				<html:radio property="obj.distribuicaoCotas" value="true" style="simDistribuicao"/> <label for="simDistribuicao"> Sim </label>
				<html:radio property="obj.distribuicaoCotas" value="false" style="naoDistribuicao"/> <label for="naoDistribuicao"> Não </label>
        	</td>
        </tr>
        <tr>
        	<th class="required">FPPI Mínimo:</th>
        	<td>
				<html:text property="fppiMinimo" size="5" maxlength="4" onkeydown="return(formataValor(this, event, 2))"/>
				<ufrn:help>Fator de Produtividade em Pesquisa Individual</ufrn:help>
        	</td>
        </tr>
        <tr>
        	<th>Divulgar Resultado?</th>
        	<td>
				<html:radio property="obj.resultadoDivulgado" value="true" style="simResultado"/> <label for="simResultado"> Sim </label>
				<html:radio property="obj.resultadoDivulgado" value="false" style="naoResultado"/> <label for="naoResultado"> Não </label>
        	</td>
        </tr>
        <tr>
        	<th class="required">Tipo da bolsa:</th>
        	<td>
        		<html:select property="tipoBolsa.id" style="width: 50%">
					<html:option value="-1"> -- SELECIONE -- </html:option>
					<html:options collection="tiposBolsa" property="id" labelProperty="descricaoResumida"/>
				</html:select>
        	</td>
        </tr>
        <tr>
        	<th class="required">Quantidade:</th>
        	<td>
        		<html:text property="quantidade" size="5" maxlength="3" onkeyup="formatarInteiro(this);"/>
        	</td>
        </tr>
        <tr>
        	<td colspan="2" align="center">
        		<html:button dispatch="adicionarCotas">Adicionar</html:button>
        	</td>
        </tr>
        <tr><td colspan="2">
        	<c:if test="${not empty formEditalPesquisa.obj.cotas}">
        	<div class="infoAltRem">
        		<html:img page="/img/delete.gif" style="overflow: visible;"/>
		    	: Remover
        	</div>
        	<table class="listagem">
        		<thead>
        			<tr>
        				<th>Tipo da bolsa</th>
        				<th>Quantidade</th>
        				<th></th>
        			</tr>
        		</thead>
        		<c:set var="totalCotas" value="0"/>
        		<c:forEach items="${formEditalPesquisa.obj.cotas}" var="cota" varStatus="row">
        			<tr>
        				<td>${cota.tipoBolsa.descricaoResumida}</td>
        				<td>${cota.quantidade}</td>
        				<td style="text-align: right;">
        					<html:link action="/pesquisa/cadastroEditalPesquisa?dispatch=removerCotas&pos=${row.index}&idCotas=${cota.id}">
								<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover" title="Remover" border="0"/>
							</html:link>
        				</td>
        				<c:set var="totalCotas" value="${totalCotas + cota.quantidade}"/>
        			</tr>
        		</c:forEach>
        		<tfoot>
        			<tr>
        				<th style="text-align: left">Total</th>
        				<th style="text-align: left">${totalCotas}</th>
        				<th></th>
        			</tr>
        		</tfoot>
        	</table>
        	</c:if>
        </td></tr>
		<tfoot>
			<tr><td colspan="2">
				<html:button dispatch="persist">${formEditalPesquisa.obj.id == 0? 'Cadastrar': 'Alterar'}</html:button>
		    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>

<c:choose>
	<c:when test="${!empty editais}">
		<br/>
		<div class="infoAltRem">
		    <html:img page="/img/view.gif" style="overflow: visible;"/>
		    : Visualizar Dados do Edital
		    <img src="/shared/img/icones/download.png" border="0" style="overflow: visible;"/>
		    : Baixar Arquivo Anexo do Edital <br/>
		    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
		    : Alterar Dados do Edital
		    <html:img page="/img/delete.gif" style="overflow: visible;"/>
		    : Remover Edital
		</div>
		<table class="listagem">
			<caption> Editais Cadastrados </caption>
			<thead>
				<tr>
					<th align="left"> Descrição </th>
					<th> Cota </th>
					<th> Período de Submissões </th>
					<th> Titulação mínima </th>
					<th colspan="4" class="nosort">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="edital" items="${editais}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${edital.descricao}</td>
					<td align="center"> ${edital.cota.descricao}</td>
					<td align="center" nowrap="nowrap">
						<ufrn:format type="data" name="edital" property="inicioSubmissao" /> a
						<ufrn:format type="data" name="edital" property="fimSubmissao" />
					</td>
					<td> ${edital.titulacaoMinimaCotasDescricao}</td>

					<td width="15">
						<html:link action="/pesquisa/cadastroEditalPesquisa?dispatch=view&id=${edital.id}">
							<img src="<%= request.getContextPath() %>/img/view.gif" alt="Visualizar Dados do Edital" title="Visualizar Dados do Edital" border="0"/>
						</html:link>
					</td>
					<td width="15">
						<a href="/sigaa/verProducao?idProducao=${edital.idArquivo}&key=${ sf:generateArquivoKey(edital.idArquivo) }" target="_blank">
						  	<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo Anexo do Edital" title="Baixar Arquivo Anexo do Edital" />
						</a>
					</td>
					<td width="15">
						<html:link action="/pesquisa/cadastroEditalPesquisa?dispatch=edit&id=${edital.id}">
							<img src="<%= request.getContextPath() %>/img/alterar.gif" alt="Alterar Dados do Edital" title="Alterar Dados do Edital" border="0"/>
						</html:link>
					</td>
					<td width="15">
	                   	<html:link action="/pesquisa/cadastroEditalPesquisa?dispatch=remove&id=${edital.id}">
	                       <img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Edital" title="Remover Edital" border="0"/>
						</html:link>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<br/>
		<p style="text-align: center; font-style: italic;">
			Nenhum Edital Cadastrado!
		</p>
	</c:otherwise>
</c:choose>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
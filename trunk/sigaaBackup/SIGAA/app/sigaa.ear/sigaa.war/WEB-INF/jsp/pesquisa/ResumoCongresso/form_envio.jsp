<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso"%>
<style>
	span.info {
		font-size: 0.9em;
		color: #555;
	}

	table.formulario td.campo {
		padding: 4px 12px;
	}

	#link-coAutores {
		line-height: 1.5em;
		background: url(${ctx}/img/pesquisa/user_add.gif) no-repeat 0 50%;
		padding-left: 20px;
		display: block;
		width: 150px;
	}

	#form-autores {
		margin: 0px 2px 5px;
	}

	#coAutores {
		margin: 0 0 5px 0;
		padding: 0;
		list-style: none;
	}

	#coAutores li{
		vertical-align: middle;
	}

	#coAutores span.nome{
		padding-right: 10px;
	}

	div.aba {
		border-bottom: 0;
	}
</style>

<script>

Resumo = {};

Resumo.adicionarCoAutor = function() {
	$('form-autores').show();
	$('link-coAutores').hide();
}

Resumo.cancelarAutor = function() {
	$('form-autores').hide();
	$('link-coAutores').show();
}

</script>

<h2>
	<ufrn:subSistema /> &gt;
	<c:out value="Submissão de Resumo para Congresso de Iniciação Científica"/>
</h2>

<c:set var="resumo" value="${formResumoCongresso.obj}" />
<div class="descricaoOperacao">
	<h3 style="text-align: center; margin-bottom: 15px;"> ${ resumo.congresso.edicao } Congresso de Iniciação Científica </h3>
	<p>
		<b>Período do Congresso:</b>
		<ufrn:format type="data" name="resumo" property="congresso.inicio" /> a
		<ufrn:format type="data" name="resumo" property="congresso.fim" />
	</p>
	<p>
		<b>Período para submissão dos resumos:</b>
		<c:set var="inicioSubmissao" value="${ formResumoCongresso.referenceData.inicioSubmissao }" />
		<c:set var="fimSubmissao" value="${ formResumoCongresso.referenceData.fimSubmissao }" />
		<ufrn:format name="inicioSubmissao" type="data" /> a
		<ufrn:format name="fimSubmissao" type="data" />
	</p>
</div>

<c:if test="${not empty avaliacao}">
	<table class="formulario" width="95%">
		<caption>Parecer do Avaliador</caption>
		<tr>
			<td colspan="2">
				<b>Possui erros gramaticais?</b> 
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format type="simnao" name="avaliacao" property="erroPortugues" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Possui erros de conteúdo?</b> 
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format type="simnao" name="avaliacao" property="erroConteudo" />
			</td>
		</tr>
		<tr>
		<td colspan="2">
				<b>Parecer</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format name="avaliacao" property="parecer" type="texto"/>
			</td>
		</tr>
	</table>
	<br />
</c:if>

<html:form action="/pesquisa/resumoCongresso" method="post" focus="obj.resumo">

	<%-- CORPO DO RESUMO --%>
    <table class="formulario" width="95%">
		<caption> Corpo do Resumo </caption>
   	    <tbody>
			<c:if test="${not empty formResumoCongresso.obj.dataEnvio }">
			<tr>
				<th nowrap="nowrap" style="text-align: left;"> <b>Última alteração em:</b> </th>
				<td>
					<ufrn:format type="dataHora" name="formResumoCongresso" property="obj.dataEnvio"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<td width="15%" align="right">
					<b>Autor:</b>
				</td>
				<td>
					${ resumo.autor.nome }
					<c:if test="${ formResumoCongresso.referenceData.exigirCPF }">
						<br />Informe o CPF do autor:
						<html:text property="cpfAutor" maxlength="14" size="14" onblur="formataCPF(this, event, null)"  />
						<span class="required">&nbsp;</span>
					</c:if>

					<c:if test="${ empty formResumoCongresso.referenceData.exigirCPF }">
					 <em> (CPF: <ufrn:format type="cpf_cnpj" name="formResumoCongresso" property="obj.autor.cpf" />) </em>
					</c:if>
				</td>
			</tr>
			<tr>
				<td align="right">
					<b>Orientador:</b>
				</td>
				<td>
					<c:choose>
						<c:when test="${ formResumoCongresso.isolado or formResumoCongresso.permissaoGestor }">
							<c:set var="idAjax" value="orientador.docente.id"/>
							<c:set var="nomeAjax" value="orientador.docente.pessoa.nome"/>
							<c:set var="obrigatorio" value="true"/>
							<c:set var="somenteInternos" value="true"/>
							<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
						</c:when>
						<c:otherwise>
							${ resumo.orientador.nome }
						</c:otherwise>
					</c:choose>

				</td>
			</tr>
			<tr>
				<td valign="top" align="right">
					<b>Co-autor(es): </b> <br/>
				</td>
				<td>
					<c:if test="${ not empty resumo.coAutores}">
					<ul id="coAutores">
						<c:forEach var="autor" items="${ resumo.autores }" varStatus="loop">
							<c:if test="${ autor.coAutor  }">
							<li>
								<span class="nome">${ autor.nome }</span>
								<html:link action="/pesquisa/resumoCongresso?dispatch=removerAutor&indice=${loop.index}">
								<img src="${ctx}/img/pesquisa/user_delete.gif"
									title="Remover este co-autor"
									alt="Remover este co-autor"/>
								</html:link>
							</li>
							</c:if>
						</c:forEach>
					</ul>
					</c:if>

					<div id="form-autores" style="display: none;">
						<html:hidden property="autor.categoria" styleId="categoriaAutor"/>

						<div id="abas-autores">
							<div id="autor-discente" class="aba">
							<table width="100%">
								<tr>
									<th width="14%" style="padding-top: 20px;">Nome:</th>
									<td>
										<c:set var="idAjax" value="autor.discente.id"/>
										<c:set var="nomeAjax" value="autor.discente.pessoa.nome"/>
										<c:set var="obrigatorio" value="true"/>
										<c:set var="opcoesNivel" value="true"/>
										<c:if test="${ usuario.discenteAtivo.tecnico }">
											<c:set var="exibeTecnico" value="true"/>
										</c:if>										
										<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_PESQUISA} %>">
											<c:set var="statusDiscente" value="todos"/>
										</ufrn:checkRole>
										<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
									</td>
								</tr>
							</table>
							</div>

							<div id="autor-docente" class="aba">
							<table width="100%">
								<tr>
									<th width="14%" class="obrigatorio">Nome:</th>
									<td>
										<c:set var="idAjax" value="autor.docente.id"/>
										<c:set var="nomeAjax" value="autor.docente.pessoa.nome"/>
										<c:set var="tipo" value="todos"/>
										<%@include file="/WEB-INF/jsp/include/ajax/servidor.jsp" %>
									</td>
								</tr>
							</table>
							</div>

							<div id="autor-externo" class="aba">
							<table width="100%">
								<tr>
									<td width="14%" class="obrigatorio" style="text-align: right; padding-right: 15px;"> Nome: </td>
									<td> <html:text property="autor.nome" style="width: 95%" /></td>
								</tr>
								<tr>
									<td class="obrigatorio" style="text-align: right; padding-right: 15px;"> E-mail: </td>
									<td> <html:text property="autor.email" style="width: 60%" /> </td>
								</tr>
								<tr>
									<td class="obrigatorio" style="text-align: right; padding-right: 15px;"> CPF (apenas números): </td>
									<td> <html:text property="cpf" maxlength="11" size="11" onkeyup="formatarInteiro(this)"  /></td>
								</tr>

							</table>
							</div>
						</div>
						<div style="text-align: center; padding: 4px; border: 1px solid #6593CF; border-top: 0; background: #C4D2EB;">
						<html:button dispatch="adicionarAutor">Adicionar</html:button>
						</div>
					</div>

					<a href="javascript://noop" id="link-coAutores" onclick="Resumo.adicionarCoAutor()">
						adicionar co-autor
					</a>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Área de Conhecimento</b>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<c:set var="grandesAreas" value="${ formResumoCongresso.referenceData.grandesAreas }"/>
					<html:select property="obj.areaConhecimentoCnpq.id" styleId="grandeArea" style="width:60%">
				        <html:options collection="grandesAreas" property="id" labelProperty="nome" />
			        </html:select>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Título</b><span class="required">&nbsp;</span>
					<span class="info"> (limitado a 200 caracteres) </span>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<ufrn:textarea property="obj.titulo" rows="2" readonly="${ requestScope.emitirparecer }" style="width:99%" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Resumo</b><span class="required">&nbsp;</span>
					<span class="info"> (limitado a 1500 caracteres) </span>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<ufrn:textarea property="obj.resumo" rows="12" readonly="${ requestScope.emitirparecer }" style="width:99%" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Palavras-Chave</b><span class="required">&nbsp;</span>
					<span class="info"> (limitado a 70 caracteres) </span>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<html:text property="obj.palavrasChave" readonly="${ requestScope.emitirparecer }" style="width:99%"/>
				</td>
			</tr>
		</tbody>
		<tr>
			<td style="padding-top: 20px;" colspan="2">
				<c:if test="${not empty resumo.correcao}">
					<table class="subFormulario" width="100%">
						<caption>Parecer do Orientador</caption>
							<tr style="margin-top: 10px;">
								<td style="color: red;">
									${resumo.correcao}
								</td>				
							</tr>
					</table>
				</c:if>
			</td>
		</tr>
		  <tfoot>
			<tr>
			  <td colspan="5">
				<div>
					<html:button dispatch="enviar">Submeter Resumo</html:button>
					<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				</div>
			 </td>
		    </tr>
		  </tfoot>
	</table>

	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

	<input type="hidden" name="id" value="${formResumoCongresso.obj.id}"/>

</html:form>


<script>
var Abas = function() {

	<c:if test="${formResumoCongresso.exibirFormCoAutor}">  
		Resumo.adicionarCoAutor();
	</c:if>  
	
	var setCategoria = function(e, aba) {
		var idAba = aba.id;
		var categoria = getEl('categoriaAutor');
		switch(idAba) {
			case 'autor-discente': categoria.dom.value = <%= AutorResumoCongresso.DISCENTE %>; break;
			case 'autor-docente': categoria.dom.value = <%= AutorResumoCongresso.DOCENTE %>; break;
			case 'autor-externo': categoria.dom.value = <%= AutorResumoCongresso.EXTERNO %>; break;
		}
	};
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-autores');
			abas.on('tabchange', setCategoria);

			abas.addTab('autor-discente', "Discente");
	        abas.addTab('autor-docente', "Docente")
			abas.addTab('autor-externo', "Externo à UFRN");

			switch( getEl('categoriaAutor').dom.value ) {
				case ''+<%=AutorResumoCongresso.DISCENTE%>:  abas.activate('autor-discente'); break;
				case ''+<%=AutorResumoCongresso.DOCENTE%>:  abas.activate('autor-docente'); break;
				case ''+<%=AutorResumoCongresso.EXTERNO%>:  abas.activate('autor-externo'); break;
				default: abas.activate('autor-discente'); break;
			}
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
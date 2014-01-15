<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
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

<f:view>

<h2><ufrn:subSistema /> &gt; Consulta de Resumos CIC</h2>

<c:set var="resumo" value="#{consultaResumoCongressoMBean.obj}" />

<div class="descricaoOperacao">
	<h3 style="text-align: center; margin-bottom: 15px;"> ${ resumo.congresso.edicao } Congresso de Iniciação Científica </h3>
	<p>
		<b>Período do Congresso:</b>
		<ufrn:format type="data" name="consultaResumoCongressoMBean" property="obj.congresso.inicio" /> a
		<ufrn:format type="data" name="consultaResumoCongressoMBean" property="obj.congresso.fim" />
	</p>
</div>


<h:form id="viewResumo">

<table class="formulario" width="95%">
<caption> Corpo do Resumo </caption>
<tbody>
	<c:if test="${not empty consultaResumoCongressoMBean.obj.dataEnvio }">
		<tr>
			<th nowrap="nowrap"> <b>Última alteração em:</b> </th>
			<td>
				<ufrn:format type="dataHora" name="consultaResumoCongressoMBean" property="obj.dataEnvio"/>
			</td>
		</tr>
	</c:if>
	<tr>
		<td width="15%">
			<b>Autor:</b>
		</td>
		<td>
			${ resumo.autor.nome }
		</td>
	</tr>
	<tr>
		<td>
			<b>Orientador:</b>
		</td>
		<td> ${ resumo.orientador.nome } </td>
	</tr>

	<c:if test="${ not empty resumo.coAutores}">
		<tr>
			<td valign="top">
				<b>Co-autor(es):</b> <br/>
			</td>
			<td>
				<ul id="coAutores">
					<c:forEach var="autor" items="${ resumo.autores }" varStatus="loop">
						<c:if test="${ autor.coAutor  }">
						<li>
							<span class="nome">${ autor.nome }</span>
						</li>
						</c:if>
					</c:forEach>
				</ul>
			</td>
		</tr>
	</c:if>

		<tr>
			<td colspan="2">
				<b>Área de Conhecimento: </b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
			${ resumo.areaConhecimentoCnpq.nome }
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Título</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				${ resumo.titulo }
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Resumo</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				${ resumo.resumo }
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Palavras-Chave</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				${ resumo.palavrasChave }
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">
				Parecer (Emitido em <ufrn:format type="datahora" name="consultaResumoCongressoMBean" property="obj.dataParecer" />)
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				${ resumo.parecer }
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnVoltar" value="<< Voltar" action="#{ consultaResumoCongressoMBean.telaBusca }"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
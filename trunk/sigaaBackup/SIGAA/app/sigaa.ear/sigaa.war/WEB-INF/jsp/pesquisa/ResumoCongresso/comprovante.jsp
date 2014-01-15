<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<h2>
	Comprovante de Envio de Resumo para Congresso de Iniciação Científica
</h2>

	<table class="subFormulario" width="100%" style="background-color: #FFFFFF;">
		<tr>
			<td valign="middle" style="text-align: justify; font-size: 1.1em;">
				<p>
					O resumo <b>${resumo.codigo} - ${ resumo.titulo }</b>, foi enviado
					para o ${resumo.congresso.edicao } Congresso de Iniciação Científica com sucesso
					em <ufrn:format type="datahora" name="resumo" property="dataEnvio" />.
				</p>
			</td>
		</tr>
	</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>